package data

trait Validator

case class Success() extends Validator

case class Failure(msg: List[String]) extends Validator
