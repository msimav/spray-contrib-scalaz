package spray.client

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.{RequestBuilding, ResponseTransformation}
import spray.util._

import scala.concurrent.duration._
import scalaz.concurrent.Task

object task extends RequestBuilding with ResponseTransformation with TransformerPipelineSupport {

  type SendReceive = HttpRequest => Task[HttpResponse]

  def sendReceive(implicit s: ActorSystem, t: Timeout = 60.seconds): SendReceive = { req =>
    Task.async { cb =>
      val transport = IO(Http)(actorSystem)
      val actor = s.actorOf(TaskActor.props(req, cb, t))
      transport.tell(req, actor)
    }
  }

}
