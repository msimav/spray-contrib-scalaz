package spray.json

import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import spray.json.Scalaz._
import spray.json.DefaultJsonProtocol._

trait Formats {

  implicit def maybeFormat[T: JsonFormat]: JsonFormat[Maybe[T]] =
    optionFormat[T].xmap(Maybe.fromOption, _.toOption)

  implicit def lazyOptionFormat[T: JsonFormat]: JsonFormat[LazyOption[T]] =
    optionFormat[T].xmap(LazyOption.fromOption, _.toOption)

  implicit def disjunctionFormat[A: JsonFormat, B: JsonFormat]: JsonFormat[A \/ B] =
    eitherFormat[A, B].xmap(\/.fromEither, _.toEither)

  implicit def lazyEitherFormat[A: JsonFormat, B: JsonFormat]: JsonFormat[A LazyEither B] =
    eitherFormat[A, B].xmap(_.fold(x => LazyEither.lazyLeft(x), x => LazyEither.lazyRight(x)), _.toEither)

  implicit def theseFormat[A: JsonFormat, B: JsonFormat]: JsonFormat[A \&/ B] = new JsonFormat[A \&/ B] {

    def write(obj: A \&/ B): JsValue =
      obj.fold(_.toJson, _.toJson, (a: A, b: B) => (a, b).toJson)

    def read(json: JsValue): A \&/ B =
      Try(json.convertTo[(A, B)].both)
        .orElse(Try(json.convertTo[A].wrapThis[B]))
        .orElse(Try(json.convertTo[B].wrapThat[A]))
        .getOrElse(deserializationError("invalid json"))

  }

  implicit def nelFormat[T: JsonFormat]: JsonFormat[NonEmptyList[T]] =
    (listFormat[T]: JsonFormat[List[T]]).xmap(xs => NonEmptyList(xs.head, xs.tail: _*), _.toList)

}
