package spray.json

import helpers.WithoutEqualizer

import scalaz._
import scalaz.Scalaz._
import spray.json.Scalaz._
import spray.http.Uri
import org.scalatest.FunSpec

class ExampleSpec extends FunSpec with WithoutEqualizer {

  describe("Example usage of JsonFormat Invariant Functor") {

    // Good old string format
    val format = DefaultJsonProtocol.StringJsonFormat
    // Uri format which is derived from string format
    val uriFormat = format.xmap(Uri.apply, (x: Uri)=> x.toString())
    // Equal instance for Uri that needed by ===
    implicit val uriEqual: Equal[Uri] = Equal.equalA[Uri]

    describe("when uri is valid") {

      val string = "http://spray.io"
      val parsed = Uri("http://spray.io")

      it("read json string as Uri object") {
        assert(uriFormat.read(JsString(string)) === parsed)
      }

      it("write uri object as json string") {
        assert(uriFormat.write(parsed) === JsString(string))
      }

    }

    describe("when uri is invalid") {

      val string = "some invalid uri"

      it("read throws IllegalUriException") {
        intercept[spray.http.IllegalUriException] {
          uriFormat.read(JsString(string))
        }
      }

    }

  }

}
