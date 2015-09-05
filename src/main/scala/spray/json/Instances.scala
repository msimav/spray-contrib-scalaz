package spray.json

import scalaz._

trait Instances {

  val JsValueCompactShow: Show[JsValue] = Show.shows[JsValue](_.compactPrint)
  implicit val JsValuePrettyShow: Show[JsValue] = Show.shows[JsValue](_.prettyPrint)
  implicit val JsValueEqual: Equal[JsValue] = Equal.equalA[JsValue]

  implicit object JsonWriterInstance extends Contravariant[JsonWriter] {
    def contramap[A, B](r: JsonWriter[A])(f: (B) => A): JsonWriter[B] =
      (obj: B) => r.write(f(obj))
  }

  implicit object JsonReaderInstance extends Functor[JsonReader] {
    def map[A, B](fa: JsonReader[A])(f: (A) => B): JsonReader[B] =
      (json : JsValue) => f(fa.read(json))
  }

  implicit object JsonFormatInstance extends InvariantFunctor[JsonFormat] {
    def xmap[A, B](ma: JsonFormat[A], f: (A) => B, g: (B) => A): JsonFormat[B] = new JsonFormat[B] {
      def read(json: JsValue): B = f(ma.read(json))
      def write(obj: B): JsValue = ma.write(g(obj))
    }
  }

}
