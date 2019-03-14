package capgemini.workshops

import cats.Monad
import scala.language.higherKinds
import cats.syntax.all._

object MonadExercises extends App {
  /**
    * F[A]
    Option[A] => Some[A]
              => None  ( Option[Nothing] )

    List[A]   => Cons :: List[A]
              => Nil

    Either[E, A] => Right[A] => result
                 => Left[E]  => exception

    map => f: A => B within a context of F[A]
    flatMap => f: A => F[B] within a context of F[A] => F[F[B]] => F[B]

    Some(value) => Some(Some(otherValue) => Some(otherValue)
    Some(value) => Some(None)            => None
    None        => None(Some(value))     => None
    Some(value) => Some(List(1,2,3)      => ???
    */

  trait Error
  case class IdNotFound()       extends Error
  case class UsernameNotFound() extends Error
  case class InvalidPassword()  extends Error

  case class UserDetails(username: String, password: String)

  type ProgramResponse[A] = Either[Error, A] // F[A]

  def recoverUsername[G[_]](id: Int)(implicit M: Monad[G]): G[String] = {
    M.pure("some-username")
  }

  def getUserDetails[G[_]](username: String)(implicit M: Monad[G]): G[UserDetails] = {
   M.pure(UserDetails("some-username", "some-password"))
  }

  def validatePassword[G[_]](password: String)(implicit M: Monad[G]): G[Unit] = {
    M.pure(())
  }

  def program[G[_]]()(implicit M: Monad[G]): G[UserDetails] = {
    for {
      username    <- recoverUsername(id = 12)
      userDetails <- getUserDetails(username)
      _           <- validatePassword(userDetails.password)
    } yield userDetails
  }
}