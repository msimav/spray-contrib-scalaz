package spray.client

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import spray.can.Http.RequestTimeoutException
import spray.client.TaskActor.Callback
import spray.http.{HttpRequest, HttpResponse}

import scalaz._
import scalaz.Scalaz._

private[client] class TaskActor(request: HttpRequest, callback: Callback, timeout: Timeout) extends Actor with ActorLogging {
  import context.dispatcher

  case object TimeoutTick
  val tt = context.system.scheduler.scheduleOnce(timeout.duration, self, TimeoutTick)
  val done = callback.andThen(_ => context.stop(self))

  def receive: Receive = {
    case response: HttpResponse =>
      log.debug("{} {} {}", response.status, request.method, request.uri)
      done(response.right)
    case TimeoutTick =>
      log.error("Request timed out after {}", timeout.duration)
      done(new RequestTimeoutException(request, "Request timed out after " + timeout.duration).left)
    case unexpected =>
      log.error("Unexpected Message: {}", unexpected)
      done(new PipelineException("Unexpected Message: " + unexpected).left)
  }

  override def postStop(): Unit = tt.cancel()

}

object TaskActor {

  type Callback = (Throwable \/ HttpResponse) => Unit

  def props(request: HttpRequest, callback: Callback, timeout: Timeout): Props =
    Props(classOf[TaskActor], request, callback, timeout)

}
