package validated

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

import cats.data._
import cats.data.Validated._
import cats.implicits._

import scala.util.{Failure, Success, Try}

object ValidatedApp extends App {

  type PersonValidator[A] = ValidatedNec[DomainErrors, A]
  sealed trait DomainErrors

  case object InvalidAge         extends DomainErrors
  case object InvalidName        extends DomainErrors
  case object InvalidDateOfBirth extends DomainErrors

  def getAge(input: Int): PersonValidator[Int] =
    Validated.condNec(input >= 0, input, InvalidAge)

  def getName(input: String): PersonValidator[String] =
    Validated.condNec(input.matches("[A-Z][a-z]*"), input, InvalidName)

  def getDateOfBirth(input: String): PersonValidator[Date] = {
    lazy val dateFormatted = new SimpleDateFormat().parse(input)
    def tryParse() = Try {
      dateFormatted
    }.isSuccess

    Validated.condNec(tryParse(), dateFormatted, InvalidDateOfBirth)
  }


  case class Person(age: Int, name: String, dateOfBirth: Date)

  def getPerson(): PersonValidator[Person] =
    (getAge(-2), getName("robert"), getDateOfBirth("asdfasdf")).mapN(Person)

  println(getPerson())
}
