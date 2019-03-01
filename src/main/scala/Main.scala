
import cats.Semigroup
import cats.laws.test.{Empty, Head, MyList}
import cats.syntax.semigroup._
import cats.syntax.functor._

object Main extends App {
  val myList: MyList[Int] = Head(2, Head(3, Empty))
  val myList2: MyList[Int] = Head(2, Head(3, Empty))

  println(Semigroup[MyList[Int]].combine(myList, myList2))

  println(myList.combine(myList2))
  println(myList.map(x => s"I give $x fucks"))
}
