# spray-contrib-scalaz [![Build Status](https://travis-ci.org/msimav/spray-contrib-scalaz.svg)](https://travis-ci.org/msimav/spray-contrib-scalaz)

Scalaz typeclass instances for spray types.

## spray-json

This package contains scalaz typeclass instances for spray-json types and
spray-json typeclass instances for scalaz data. 

#### Scalaz instances

* Functor instance for JsonReader
* Contravariant instance for JsonWriter
* Invariant Functor instance for JsonReader
* Show and Equal instances for JsValue

Example usage is at [ExampleSpec](https://github.com/msimav/spray-contrib-scalaz/blob/master/src/test/scala/spray/json/ExampleSpec.scala).

#### Spray JsonFormat instances

* Maybe
* LazyOption
* Disjunction - A \/ B
* LazyEither
* These - A \\&/ B
* NonEmptyList

## spray-client

This package contains sendReceive implementation with `scalaz.concurrent.Task`.

```scala
import spray.client.task._

implicit val system = ActorSystem("test")
val pipeline: HttpRequest => Task[HttpResponse] = sendReceive

val response: Task[HttpResponse] = pipeline(Get("http://spray.io"))

response.run
```

Example usage is at [SendReceiveSpec](https://github.com/msimav/spray-contrib-scalaz/blob/master/src/test/scala/spray/client/SendReceiveSpec.scala).
