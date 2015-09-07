package spray.client

import helpers.WithoutEqualizer
import org.scalatest.FunSpec
import org.scalatest.prop.Checkers

import scalaz.Scalaz._
import scalaz.concurrent.Task

class TransformerPipelineSupportSpec extends FunSpec with Checkers
  with TransformerPipelineSupport with WithoutEqualizer {

  describe("(A => Task[B]) ~> (B => C) => A => Task[C]") {

    val f: Int => Task[String] = i => Task.delay(i.toString)
    val g: String => Int = _.toInt

    it("auxTaskMap") {
      val fog = auxTaskMap(f, g)
      check { input: Int =>
        fog(input).run === input
      }
    }

    it("f ~> g") {
      import spray.httpx.TransformerPipelineSupport._
      val fog = f ~> g
      check { input: Int =>
        fog(input).run === input
      }
    }

  }

  describe("(A => Task[B]) ~> (B => Task[C]) => A => Task[C]") {

    val f: Int => Task[String] = i => Task.delay(i.toString)
    val g: String => Task[Int] = s => Task.delay(s.toInt)

    it("auxTaskFlatMap") {
      val fog = auxTaskFlatMap(f, g)
      check { input: Int =>
        fog(input).run === input
      }
    }

    it("f ~> g") {
      import spray.httpx.TransformerPipelineSupport._
      val fog = f ~> g
      check { input: Int =>
        fog(input).run === input
      }
    }


  }

}
