
package workshop

import cats.data.{EitherT, NonEmptyList}
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
import scala.concurrent.Await

object FirstPart extends App {
  case class Person(name: String, age: Int)

  val data: Map[String, String] = Map("name" -> "Test", "age" -> "22")

  type Err = NonEmptyList[String]

  type MyResult[A] = EitherT[Future, Err, A]

  def callNameService(name: String): Future[Boolean] = Future.successful(name != "Test")

  def readPerson(data: Map[String, String]): MyResult[Person] = {
    (getName(data), getAge(data)).parMapN((name, age) => Person(name, age))
  }

  def getAge(data: Map[String, String]): MyResult[Int] = {
    readAge(data).flatMap(age => (notMinor(age), realAge(age)).parMapN((_, _) => age))
  }

  def getName(data: Map[String, String]): MyResult[String] = {
    for {
      name <- readName(data)
      _    <- realName(name)
      _    <- checkName(name)
    } yield name
  }

  def readName(data: Map[String, String]): MyResult[String] = getField("name")(data)

  def checkName(name: String): MyResult[String] = EitherT.liftF[Future, Err, Boolean] {
    callNameService(name)
  }.flatMap {
    ok => if(ok) name.pure[MyResult] else NonEmptyList.of("Badness").raiseError[MyResult, String]
  }

  def readAge(data: Map[String, String]): MyResult[Int] = {
    getField("age")(data).flatMap(stringToInt)
  }

  def realAge(age: Int): MyResult[Unit] =
    if(age > 0) ().pure[MyResult]
    else NonEmptyList.of("Negative age").raiseError[MyResult, Unit]

  def realName(name: String): MyResult[Unit] =
    if(name.nonEmpty) ().pure[MyResult]
    else NonEmptyList.of("Fake name").raiseError[MyResult, Unit]

  def notMinor(age: Int)    : MyResult[Unit] =
    if(age > 18) ().pure[MyResult]
    else NonEmptyList.of("Minor").raiseError[MyResult, Unit]

  def getField(field: String)(data: Map[String, String]): MyResult[String] = {
    data.get(field) match {
      case None      => NonEmptyList.of(s"Field $field not found").raiseError[MyResult, String]
      case Some(str) => str.pure[MyResult]
    }
  }

  def stringToInt(str: String): MyResult[Int] = {
    Try {
      str.toInt
    } match {
      case Failure(_) => NonEmptyList.of(s"$str was not a valid int").raiseError[MyResult, Int]
      case Success(v) => v.pure[MyResult]
    }
  }

  println(Await.result(readPerson(data).value, Duration.fromNanos(5000)))
}
