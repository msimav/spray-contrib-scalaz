package helpers

import org.scalactic.TripleEqualsSupport

trait WithoutEqualizer extends TripleEqualsSupport {

  // I don't want it to interfere with Scalaz.Equal
  override def convertToEqualizer[T](left: T): Equalizer[T] = ???

}
