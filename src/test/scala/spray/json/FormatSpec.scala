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

    it("just") {
      check { input: String =>
        (maybeFormat[String].read(JsString(input)) === input.just) &&
          (maybeFormat[String].write(input.just) === JsString(input))
      }
    }

    it("none") {
      assert(maybeFormat[String].read(JsNull) === Maybe.empty[String])
    }

  }

  describe("lazyOptionFormat") {

    it("lazySome") {
      check { input: String =>
        (lazyOptionFormat[String].read(JsString(input)) === LazyOption.lazySome(input)) &&
          (lazyOptionFormat[String].write(LazyOption.lazySome(input)) === JsString(input))
      }
    }

    it("lazyNone") {
      assert(lazyOptionFormat[String].read(JsNull) === LazyOption.lazyNone)
    }

  }

  describe("disjunctionFormat") {

    it("left") {
      check { input: String =>
        (disjunctionFormat[String, Int].read(JsString(input)) === input.left) &&
          (disjunctionFormat[String, Int].write(input.left) === JsString(input))
      }
    }

    it("right") {
      check { input: Int =>
        (disjunctionFormat[String, Int].read(JsNumber(input)) === input.right) &&
          (disjunctionFormat[String, Int].write(input.right) === JsNumber(input))
      }
    }

  }

  describe("lazyEitherFormat") {

    it("lazyLeft") {
      check { input: String =>
        (lazyEitherFormat[String, Int].read(JsString(input)).disjunction === input.left) &&
          (lazyEitherFormat[String, Int].write(LazyEither.lazyLeft(input)) === JsString(input))
      }
    }

    it("lazyRight") {
      check { input: Int =>
        (lazyEitherFormat[String, Int].read(JsNumber(input)).disjunction === input.right) &&
          (lazyEitherFormat[String, Int].write(LazyEither.lazyRight(input)) === JsNumber(input))
      }
    }

  }

  describe("theseFormat") {

    it("this") {
      check { input: String =>
        (theseFormat[String, Int].read(JsString(input)) === input.wrapThis) &&
          (theseFormat[String, Int].write(input.wrapThis) === JsString(input))
      }
    }

    it("that") {
      check { input: Int =>
        (theseFormat[String, Int].read(JsNumber(input)) === input.wrapThat) &&
          (theseFormat[String, Int].write(input.wrapThat) === JsNumber(input))
      }
    }

    it("both") {
      check { (in1: String, in2: Int) =>
        val array = JsArray(JsString(in1), JsNumber(in2))
        (theseFormat[String, Int].read(array) === (in1, in2).both) &&
          (theseFormat[String, Int].write((in1, in2).both) === array)
      }
    }

  }

  describe("nelFormat") {

    it("list with at least 1 element") {
      check { (head: String, tail: List[String]) =>
        val array = JsArray((head :: tail).map(JsString.apply).toVector)
        (nelFormat[String].read(array) === NonEmptyList(head, tail: _*)) &&
          (nelFormat[String].write(NonEmptyList(head, tail: _*)) === array)
      }
    }

    it("empty list") {
      intercept[NoSuchElementException] {
        nelFormat[String].read(JsArray.empty) === NonEmptyList("this", "test", "fails")
      }
    }

  }

}
