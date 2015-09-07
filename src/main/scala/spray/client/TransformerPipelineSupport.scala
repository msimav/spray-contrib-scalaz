package spray.client

import scalaz.concurrent.Task
import spray.httpx.TransformerAux

trait TransformerPipelineSupport extends HighPriorityImplicits

private[client] trait HighPriorityImplicits extends LowPriorityImplicits {

  implicit def auxTaskFlatMap[A, B, C]: TransformerAux[A, Task[B], B, Task[C], Task[C]] =
    new TransformerAux[A, Task[B], B, Task[C], Task[C]] {
      def apply(f: A => Task[B], g: B => Task[C]): A => Task[C] = f(_).flatMap(g)
    }

}

private[client] trait LowPriorityImplicits {

  implicit def auxTaskMap[A, B, C]: TransformerAux[A, Task[B], B, C, Task[C]] =
    new TransformerAux[A, Task[B], B, C, Task[C]] {
      def apply(f: A => Task[B], g: B => C): A => Task[C] = f(_).map(g)
    }

}
