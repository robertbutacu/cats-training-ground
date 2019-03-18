package capgemini.workshops

import cats.{Monad, MonadError}

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

  trait MyError extends Throwable
  case class IdNotFound()       extends MyError
  case class UsernameNotFound() extends MyError
  case class InvalidPassword()  extends MyError

  case class UserDetails(username: String, password: String)

  type ProgramMonadError[F[_]] = MonadError[F, Throwable] // F[A]

  def recoverUsername[G[_]](id: Int)(implicit M: ProgramMonadError[G]): G[String] = {
    if(id % 2 == 0) M.pure("some-username")
    else            M.raiseError(IdNotFound())
  }

  def getUserDetails[G[_]](username: String)(implicit M: ProgramMonadError[G]): G[UserDetails] = {
   if(username == "some-username") M.pure(UserDetails("some-username", "some-password"))
   else                            M.raiseError(UsernameNotFound())
  }

  def validatePassword[G[_]](password: String)(implicit M: ProgramMonadError[G]): G[Unit] = {
    if(password == "some-password") M.pure(())
    else                            M.raiseError(InvalidPassword())
  }

  def program[G[_]]()(implicit M: ProgramMonadError[G]): G[UserDetails] = {
    for {
      username    <- recoverUsername(id = 12)
      userDetails <- getUserDetails(username)
      _           <- validatePassword(userDetails.password)
    } yield userDetails
  }
}