package imap

import cats.Monoid
import cats.instances.int._
import cats.instances.invariant._
import cats.instances.list._
import cats.instances.string._
import cats.syntax.apply._

object Imap extends App {
  case class Cat(
                  name: String,
                  yearOfBirth: Int,
                  favoriteFoods: List[String]
                )
  // for Monoid
  // for Semigroupal
  // for Monoid
  // for Monoid
  // for imapN
  val tupleToCat: (String, Int, List[String]) => Cat = Cat.apply _


  val catToTuple: Cat => (String, Int, List[String]) = cat => (cat.name, cat.yearOfBirth, cat.favoriteFoods)
  implicit val catMonoid: Monoid[Cat] = (
    Monoid[String],
    Monoid[Int],
    Monoid[List[String]]
  ).imapN(tupleToCat)(catToTuple)
}
