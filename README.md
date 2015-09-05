# spray-contrib-scalaz

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
