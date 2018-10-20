package semigroup

import cats.instances.all._
import cats.syntax.semigroup._
import cats.kernel._
import scala.io.StdIn.readLine

object SemigroupApp extends App {

  //Semigroup has the combine method, taking 2 A's and returning an A
  trait Validate

  case object Succesful extends Validate

  case class Failure(msg: List[String]) extends Validate

  implicit def validateSemigroup: Semigroup[Validate] = (x: Validate, y: Validate) => x match {
    case Succesful => y
    case Failure(msg) => y match {
      case Succesful => x
      case Failure(msg2) => Failure(Semigroup[List[String]].combine(msg, msg2))
    }
  }

  def readFromKeyboard(implicit S: Semigroup[Validate]) = {
    def categorise(msg: String, fallback: String = "Dambas fuck") =
      if (msg == "Hi" || msg == "Hello") Succesful
      else Failure(List(fallback))

    val msg = readLine("Write something")
    val msg2 = readLine("Write something else")

    //interestingly enough, combineAllOption in Semigroup is created to simulate the fold function
    // however, since Semigroup has no empty elemenent, then Option is used to do that
    println(S.combineAllOption(List(categorise("1"), categorise("1"), categorise("1"), categorise("1"))))
    println(S.combineAllOption(List()))
    println(Semigroup[Int].combineN(10, 10))

    S.combine(categorise(msg, "Wrong line\n"), categorise(msg2, "Wrong line again motherfucker\n"))
  }


  println(readFromKeyboard)
}
