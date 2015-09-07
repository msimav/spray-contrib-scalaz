package spray.client

import akka.actor.ActorSystem
import helpers.WithoutEqualizer
import org.scalatest.FunSpec
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

class SendReceiveSpec extends FunSpec with SprayJsonSupport with WithoutEqualizer {

  it("Example usage of spray.client.task") {
    // just import spray.client.task._ instead of spray.client.pipelining._
    import spray.client.task._

    case class Location(latitude: Double, longitude: Double)
    case class Elevation(location: Location, elevation: Double)
    case class GoogleApiResult[T](status: String, results: List[T])

    implicit val locationFormat = jsonFormat(Location.apply, "lat", "lng")
    implicit val elevationFormat = jsonFormat2(Elevation)
    implicit def googleApiResultFormat[T: JsonFormat] = jsonFormat2(GoogleApiResult.apply[T])

    implicit val system = ActorSystem("test")

    // We just care only first elevation on the list
    val takeFirstElevation: GoogleApiResult[Elevation] => Option[Elevation] = _.results.headOption

    // make request, unmarshall response and take first elevation from the list
    val pipeline = sendReceive ~> unmarshal[GoogleApiResult[Elevation]] ~> takeFirstElevation

    val response = pipeline(Get("http://maps.googleapis.com/maps/api/elevation/json?locations=27.988056,86.925278&sensor=false"))

    // Tasks are lazy, we need to run when we need their result
    val taskResult = response.attemptRun
    taskResult.fold(
      error => println("Error: " + error.getMessage),
      success => println("Result: " + success)
    )

    assert(taskResult.isRight)
    assert(taskResult.exists(_.contains(Elevation(Location(27.988056, 86.925278), 8815.7158203125))))

    system.shutdown()

  }

}
