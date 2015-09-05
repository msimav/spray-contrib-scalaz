package spray.json.laws

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.scalacheck._
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.json.Scalaz._

import scalaz._
import scalaz.Scalaz._

object JsonReaderLaws {

  val laws = new Properties("JsonReader Functor Laws") {

    val laws = Functor[JsonReader].functorLaw

    val intReader: JsonReader[Int] = IntJsonFormat

    implicit val jsNumberArbitrary: Arbitrary[JsNumber] =
      Arbitrary(arbitrary[Int].map(JsNumber.apply))

    implicit val intFunctionArbitrary: Arbitrary[Int => Int] =
      Arbitrary(Gen.oneOf((x: Int) => -x, (x: Int) => x % 5, (x: Int) => x * x, (x: Int) => x, (x: Int) => 3 * x, (x: Int) => x + 1))

    def equalsOn[T: Equal](input: JsValue): Equal[JsonReader[T]] = new Equal[JsonReader[T]] {
      def equal(a1: JsonReader[T], a2: JsonReader[T]): Boolean =
        a1.read(input) === a2.read(input)
    }


    property("identity") = forAll { input: JsNumber =>
      laws.identity(intReader)(equalsOn(input))
    }

    property("composite") = forAll { (input: JsNumber, f: (Int => Int), g: (Int => Int)) =>
      laws.composite(intReader, f, g)(equalsOn(input))
    }

    property("invariantIdentity") = forAll { input: JsNumber =>
      laws.invariantIdentity(intReader)(equalsOn(input))
    }

    property("invariantComposite") = forAll { (input: JsNumber, f1: (Int => Int), g1: (Int => Int), f2: (Int => Int), g2: (Int => Int)) =>
      laws.invariantComposite(intReader, f1, g1, f2, g2)(equalsOn(input))
    }

  }

}
