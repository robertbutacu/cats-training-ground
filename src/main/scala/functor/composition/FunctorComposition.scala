package functor.composition

import cats.Functor
import cats.implicits._

object FunctorComposition extends App {
  val listOptionFunctor = Functor[List].compose(Functor[Option])


  println(listOptionFunctor.map(List(Option(2), Option(3), None))((x: Int) => x + 1))

}
