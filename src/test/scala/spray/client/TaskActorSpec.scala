package spray.client

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helpers.WithoutEqualizer
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}
import spray.can.Http.RequestTimeoutException
import spray.client.TaskActor.Callback
import spray.http.{HttpResponse, HttpRequest}

import scala.concurrent.duration._
import scalaz._
import scalaz.Scalaz._

class TaskActorSpec extends TestKit(ActorSystem("TestActorSpec")) with FunSpecLike
   with BeforeAndAfterAll with WithoutEqualizer {

  val request = HttpRequest()
  val callback: Callback = msg => testActor ! msg // forward all messages to testActor
  val timeout = 60.seconds

  describe("when received HttpResponse") {

    val response = HttpResponse()
    val actor = system.actorOf(TaskActor.props(request, callback, timeout))

    it("call callback with response.right") {
      actor ! response
      expectMsg(response.right[Throwable])
    }

  }

  describe("when timed out") {

    system.actorOf(TaskActor.props(request, callback, 1.second))

    it("call callback with RequestTimeoutException.right") {
      val msg = expectMsgType[Throwable \/ HttpResponse]

      assert(msg.isLeft)
      val -\/(error: RequestTimeoutException) = msg
      assert(error.request == request) // They are different types so can't compare with === :(
      assert(error.getMessage === "Request timed out after 1 second")
    }

  }

  describe("when received unexpected") {

    val actor = system.actorOf(TaskActor.props(request, callback, timeout))

    it("call callback with PipelineException.right") {
      actor ! "unexpected"

      val msg = expectMsgType[Throwable \/ HttpResponse]

      assert(msg.isLeft)
      val -\/(error: PipelineException) = msg
      assert(error.getMessage === "Unexpected Message: unexpected")
    }

  }


  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

}
