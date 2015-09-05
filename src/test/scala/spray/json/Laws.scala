package spray.json

import org.scalacheck._
import spray.json.laws._

object Laws extends Properties("All Laws") {

  include(JsonReaderLaws.laws)
  include(JsonWriterLaws.laws)
  include(JsonFormatLaws.laws)

}
