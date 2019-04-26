package workshop

import cats.data.NonEmptyList
import cats.implicits._

object Main extends App {
  case class Person(name: String, age: Int)

  val data: Map[String, String] = Map("name" -> "Test", "age" -> "-7")

  type Err = NonEmptyList[String]

  def readPerson(data: Map[String, String]): Either[Err, Person] = {
    (getName(data), getAge(data)).parMapN((name, age) => Person(name, age))
  }

  def getAge(data: Map[String, String]): Either[Err, Int] = {
    readAge(data).flatMap(age => (notMinor(age), realAge(age)).parMapN((_, _) => age))
  }

  def getName(data: Map[String, String]): Either[Err, String] = {
    readName(data).flatMap(name => realName(name).map(_ => name))
  }

  def readName(data: Map[String, String]): Either[Err, String] = {
    getField("name")(data)
  }

  def readAge(data: Map[String, String]): Either[Err, Int] = {
    getField("age")(data).right.flatMap(stringToInt)
  }

  def realAge(age: Int)     : Either[Err, Unit] = Either.cond(age > 0,       (), NonEmptyList.of("Negative age"))
  def realName(name: String): Either[Err, Unit] = Either.cond(name.nonEmpty, (), NonEmptyList.of("Illegal name"))
  def notMinor(age: Int)    : Either[Err, Unit] = Either.cond(age > 18,      (), NonEmptyList.of("Minor"))

  def getField(field: String)(data: Map[String, String]): Either[Err, String] = {
    data.get(field).toRight(NonEmptyList.of(s"Field $field not found"))
  }

  def stringToInt(str: String): Either[Err, Int] = {
    Either.catchOnly[IllegalArgumentException](str.toInt).leftMap(_ => NonEmptyList.of(s"$str was not a valid int"))
  }

  println(readPerson(data))
}
