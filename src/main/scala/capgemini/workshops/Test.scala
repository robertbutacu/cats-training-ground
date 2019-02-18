package capgemini.workshops

import SemigroupApp.Semigroup.SemigroupOps
object Test extends App {
    println(2 |+| 3)
    println("abc" |+| "asdas")

  println(List(1,2,3,4).reduce(_ + _))
  println(List.empty[Int].reduce(_ + _))

  val nonEmptyList = List(1)
  val nonEmptyList2 = List(1,2,3)

  println(((nonEmptyList ++ nonEmptyList2) == (nonEmptyList2 ++ nonEmptyList)) == nonEmptyList2)
}
