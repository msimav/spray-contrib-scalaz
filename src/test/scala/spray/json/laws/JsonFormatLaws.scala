package spray.json.laws

import org.scalacheck.Prop.forAll
import org.scalacheck._
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.json.Scalaz._

import scalaz._
import scalaz.Scalaz._

object JsonFormatLaws {

  val laws = new Properties("JsonFormat Invariant Functor Laws") {

    val laws = InvariantFunctor[JsonFormat].invariantFunctorLaw

    implicit val intFunctionArbitrary: Arbitrary[Int => Int] =
      Arbitrary(Gen.oneOf((x: Int) => -x, (x: Int) => x % 5, (x: Int) => x * x, (x: Int) => x, (x: Int) => 3 * x, (x: Int) => x + 1))

    def equalsOn(input: Int): Equal[JsonFormat[Int]] = new Equal[JsonFormat[Int]] {
      def equal(a1: JsonFormat[Int], a2: JsonFormat[Int]): Boolean =
        (a1.read(JsNumber(input)) === a2.read(JsNumber(input))) && (a1.write(input) === a2.write(input))
    }


    property("invariantIdentity") = forAll { input: Int =>
      laws.invariantIdentity(IntJsonFormat)(equalsOn(input))
    }

    property("invariantComposite") = forAll { (input: Int, f1: (Int => Int), g1: (Int => Int), f2: (Int => Int), g2: (Int => Int)) =>
      laws.invariantComposite(IntJsonFormat, f1, g1, f2, g2)(equalsOn(input))
    }

  }

}
