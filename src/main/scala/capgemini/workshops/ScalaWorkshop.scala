/*
import cats.effect.IO
import cats.{Applicative, Monad, MonadError}
import cats.implicits._

import scala.concurrent.Future
import scala.language.higherKinds
import scala.util.Try

object ScalaWorkshop {

  trait Error extends Throwable
  case class UserNameNotFound() extends Error
  case class IdNotFound()       extends Error
  case class InvalidPassword()  extends Error

  type Username = String
  type Password = String

  case class UserDetails(username: Username, password: Password)

  type ProgramType[F[_]] = MonadError[F, Throwable]

  def recoverUsername[F[_]](id: Int)(implicit M: ProgramType[F]): F[String] = id match {
    case 1 => M.pure("Success")
    case _ => M.raiseError(IdNotFound())
  }

  def getUserDetails[F[_]](username: String)(implicit M: ProgramType[F]): F[UserDetails] = username match {
    case "john" => M.pure(UserDetails("john", "bing"))
    case _      => M.raiseError(UserNameNotFound())
  }

  def validatePassword[F[_]](password: String)(implicit M: ProgramType[F]): F[Unit] = password match {
    case "bosh" => M.pure(())
    case _      => M.raiseError(InvalidPassword())
  }

  def program[F[_]](implicit M: ProgramType[F]):  F[UserDetails] =
    for {
      x           <- recoverUsername(1)
      userDetails <- getUserDetails("john")
      z           <- validatePassword("bosh")
    } yield userDetails
}


object Main2 {
  ScalaWorkshop.program[Either]
  ScalaWorkshop.program[Try]
  ScalaWorkshop.program[Future]
  ScalaWorkshop.program[IO]
  ScalaWorkshop.program[scalaz.zio.IO]
}*/
