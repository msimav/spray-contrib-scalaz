package spray.json

import helpers.WithoutEqualizer
import org.scalatest.prop.Checkers

import scalaz._
import scalaz.Scalaz._
import spray.json.Scalaz._
import spray.json.DefaultJsonProtocol._
import org.scalacheck._
import org.scalatest.FunSpec

class FormatSpec extends FunSpec with Checkers with WithoutEqualizer {

  describe("maybeFormat") {

    val f = implicitly[JsonFormat[Maybe[String]]]

    it("just") {
      check { input: String =>
        (f.read(JsString(input)) === input.just) && (f.write(input.just) === JsString(input))
      }
    }

    it("none") {
      assert(f.read(JsNull) === Maybe.empty[String])
    }

  }

  describe("lazyOptionFormat") {

    val f = implicitly[JsonFormat[LazyOption[String]]]

    it("lazySome") {
      check { input: String =>
        (f.read(JsString(input)) === LazyOption.lazySome(input)) &&
          (f.write(LazyOption.lazySome(input)) === JsString(input))
      }
    }

    it("lazyNone") {
      assert(f.read(JsNull) === LazyOption.lazyNone)
    }

  }

  describe("disjunctionFormat") {

    val f = implicitly[JsonFormat[String \/ Int]]

    it("left") {
      check { input: String =>
        (f.read(JsString(input)) === input.left) && (f.write(input.left) === JsString(input))
      }
    }

    it("right") {
      check { input: Int =>
        (f.read(JsNumber(input)) === input.right) && (f.write(input.right) === JsNumber(input))
      }
    }

  }

  describe("lazyEitherFormat") {

    val f = implicitly[JsonFormat[LazyEither[String, Int]]]

    it("lazyLeft") {
      check { input: String =>
        (f.read(JsString(input)).disjunction === input.left) &&
          (f.write(LazyEither.lazyLeft(input)) === JsString(input))
      }
    }

    it("lazyRight") {
      check { input: Int =>
        (f.read(JsNumber(input)).disjunction === input.right) &&
          (f.write(LazyEither.lazyRight(input)) === JsNumber(input))
      }
    }

  }

  describe("theseFormat") {

    val f = implicitly[JsonFormat[String \&/ Int]]

    it("this") {
      check { input: String =>
        (f.read(JsString(input)) === input.wrapThis) && (f.write(input.wrapThis) === JsString(input))
      }
    }

    it("that") {
      check { input: Int =>
        (f.read(JsNumber(input)) === input.wrapThat) && (f.write(input.wrapThat) === JsNumber(input))
      }
    }

    it("both") {
      check { (in1: String, in2: Int) =>
        val array = JsArray(JsString(in1), JsNumber(in2))
        (f.read(array) === (in1, in2).both) && (f.write((in1, in2).both) === array)
      }
    }

  }

  describe("nelFormat") {

    val f = implicitly[JsonFormat[NonEmptyList[String]]]

    it("list with at least 1 element") {
      check { (head: String, tail: List[String]) =>
        val array = JsArray((head :: tail).map(JsString.apply).toVector)
        (f.read(array) === NonEmptyList(head, tail: _*)) && (f.write(NonEmptyList(head, tail: _*)) === array)
      }
    }

    it("empty list") {
      intercept[NoSuchElementException] {
        f.read(JsArray.empty) === NonEmptyList("this", "test", "fails")
      }
    }

  }

}
