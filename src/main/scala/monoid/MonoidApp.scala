package monoid

import cats._
import _root_.data.Validator
import _root_.data.Success
import semigroup.SemigroupApp.validateSemigroup

object MonoidApp extends App {
  implicit val validatorMonoid: Monoid[Validator] = new Monoid[Validator] {
    override def empty: Validator = Success()

    override def combine(x: Validator, y: Validator): Validator = Semigroup[Validator].combine(x, y)
  }
}
