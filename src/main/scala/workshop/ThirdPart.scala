package workshop
import cats.MonadError

import scala.language.higherKinds
import cats.instances.try_._
import scala.util.Try
// Main.scala

object ThirdPart extends App {
  def createDatabase[F[_]](implicit M: MonadError[F, Throwable]): PersonDatabase[F] = PersonDatabase.createTestDatabase[F]

  val personSerivce = new PersonService[Try](createDatabase[Try])

  println(personSerivce.isFriends("Alice", "Bob"))
  println(personSerivce.isFriends("Alcccice", "Bob"))
  println(personSerivce.isFriends("Alice", "Bobb"))
  println(personSerivce.nTransitiveFriends("Alice", 5))
  println(personSerivce.nTransitiveFriends("Alice", 1))
}

// Person.scala

case class Person(name: String, favoriteFood: String, friends: List[String])

// PersonDatabase.scala

sealed trait MyError extends Throwable
case object PersonNotFound extends MyError


class PersonDatabase[F[_]](people: List[Person])(implicit M: MonadError[F, Throwable]) {
  def find(name: String): F[Person] = {
    people.find(_.name == name).fold(M.raiseError[Person](PersonNotFound))(p => M.pure(p))
  }
}

object PersonDatabase {
  def createTestDatabase[F[_]](implicit M: MonadError[F, Throwable]): PersonDatabase[F] =
    new PersonDatabase[F](List(
      Person(name = "Alice"   , favoriteFood = "Pizza"     , friends = List("Bob",    "Gina"    )),
      Person(name = "Bob"     , favoriteFood = "Ice cream" , friends = List("Iris",   "Liam"    )),
      Person(name = "Charlie" , favoriteFood = "Roast"     , friends = List("Bob",    "Iris"    )),
      Person(name = "Declan"  , favoriteFood = "Pizza"     , friends = List("Gina",   "Juan"    )),
      Person(name = "Erica"   , favoriteFood = "Ice cream" , friends = List("Iris",   "Charlie" )),
      Person(name = "Frank"   , favoriteFood = "Roast"     , friends = List("Liam",   "Henrick" )),
      Person(name = "Gina"    , favoriteFood = "Pizza"     , friends = List("Declan", "Bob"     )),
      Person(name = "Henrick" , favoriteFood = "Ice cream" , friends = List("Declan", "Erica"   )),
      Person(name = "Iris"    , favoriteFood = "Pizza"     , friends = List("Kevin",  "Frank"   )),
      Person(name = "Juan"    , favoriteFood = "Ice cream" , friends = List("Alice",  "Bob"     )),
      Person(name = "Kevin"   , favoriteFood = "Roast"     , friends = List("Alice",  "Gina"    )),
      Person(name = "Liam"    , favoriteFood = "Pizza"     , friends = List("Erica",  "Frank"   )),
    ))
}