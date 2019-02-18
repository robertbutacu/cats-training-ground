package monadic.design

import java.util.UUID

import cats.data.EitherT
import cats.instances.future._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

//the idea is to design a system with a fail-first error handling
object MonadicDesign extends App {

  trait WebPageResponse

  case object Success             extends WebPageResponse
  case object Timeout             extends WebPageResponse
  case object BadRequest          extends WebPageResponse
  case object InternalServerError extends WebPageResponse
  case object Redirected          extends WebPageResponse
  case object Conflict            extends WebPageResponse
  case object BadGateway          extends WebPageResponse
  case object NotFound            extends WebPageResponse

  def probabilityForInternalServerError: EitherT[Future, WebPageResponse, Unit] = EitherT {
    Random.nextInt % 2 match {
      case 0 => Future.successful(Right(()))
      case _ => Future.successful(Left(InternalServerError))
    }
  }


  type Username = String
  def recoverUsername(id: Int): EitherT[Future, WebPageResponse, Username] = EitherT {
    id % 2 match {
      case 0 => Future.successful(Right(UUID.randomUUID().toString))
      case _ => Future.successful(Left(NotFound))
    }
  }

  type Password = String
  case class UserDetails(username: Username, password: Password)

  def getUserDetails(username: Username): EitherT[Future, WebPageResponse, UserDetails] = {
    username.hashCode() % 2 match {
      case 0 => probabilityForInternalServerError.map(_ => UserDetails(username, UUID.randomUUID().toString))
      case _ => EitherT(Future.successful[Either[WebPageResponse, UserDetails]](Left(BadRequest)))
    }
  }

  def validatePassword(password: Password): EitherT[Future, WebPageResponse, Unit] = {
    password.hashCode() % 2 match {
      case 0 => probabilityForInternalServerError.map(_ => ())
      case _ => EitherT(Future.successful[Either[WebPageResponse, Unit]](Left(Conflict)))
    }
  }

  def run() = {
    (1 to 100). map { _ =>
      for {
        username    <- recoverUsername(Random.nextInt())
        userDetails <- getUserDetails(username)
        _           <- validatePassword(userDetails.password)
      } yield ()
    } .map(v => Await.result( v.value, 10 seconds))
      .foreach(println)
  }

  run()
}
