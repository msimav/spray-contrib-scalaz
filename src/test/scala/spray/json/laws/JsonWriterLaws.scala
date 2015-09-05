package spray.json.laws

import org.scalacheck.Prop.forAll
import org.scalacheck._
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.json.Scalaz._

import scalaz._
import scalaz.Scalaz._

object JsonWriterLaws {

  val laws = new Properties("JsonWriter Contravariant Laws") {

    val laws = Contravariant[JsonWriter].contravariantLaw

    val intWriter: JsonWriter[Int] = IntJsonFormat

    implicit val intFunctionArbitrary: Arbitrary[Int => Int] =
      Arbitrary(Gen.oneOf((x: Int) => -x, (x: Int) => x % 5, (x: Int) => x * x, (x: Int) => x, (x: Int) => 3 * x, (x: Int) => x + 1))

    def equalsOn[T](input: T): Equal[JsonWriter[T]] = new Equal[JsonWriter[T]] {
      def equal(a1: JsonWriter[T], a2: JsonWriter[T]): Boolean =
        a1.write(input) === a2.write(input)
    }


    property("identity") = forAll { input: Int =>
      laws.identity(intWriter)(equalsOn(input))
    }

    property("composite") = forAll { (input: Int, f: (Int => Int), g: (Int => Int)) =>
      laws.composite(intWriter, f, g)(equalsOn(input))
    }

    property("invariantIdentity") = forAll { input: Int =>
      laws.invariantIdentity(intWriter)(equalsOn(input))
    }

    property("invariantComposite") = forAll { (input: Int, f1: (Int => Int), g1: (Int => Int), f2: (Int => Int), g2: (Int => Int)) =>
      laws.invariantComposite(intWriter, f1, g1, f2, g2)(equalsOn(input))
    }

  }

}
