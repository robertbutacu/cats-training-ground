package data

trait Validator

case object Successful extends Validator

case class Failure(msg: List[String]) extends Validator
