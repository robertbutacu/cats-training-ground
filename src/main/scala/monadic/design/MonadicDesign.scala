package monadic.design

import java.util.UUID

import cats.data.{EitherT, WriterT}
import cats.instances.future._
import cats.syntax.writer._
import cats.data.Writer._
import cats.instances.vector._
import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

//the idea is to design a system with a fail-first error handling
object MonadicDesign extends App {

  type FutureEitherT[A] = EitherT[Future, WebPageResponse, A]
  type LoggingWithEitherT[F[_], A] = WriterT[F, Vector[String], A]

  trait WebPageResponse

  case object Success extends WebPageResponse

  case object Timeout extends WebPageResponse

  case object BadRequest extends WebPageResponse

  case object InternalServerError extends WebPageResponse

  case object Redirected extends WebPageResponse

  case object Conflict extends WebPageResponse

  case object BadGateway extends WebPageResponse

  case object NotFound extends WebPageResponse

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

  def lift[A](v: FutureEitherT[A], log: Vector[String]): WriterT[FutureEitherT, Vector[String], A] = {
    WriterT(v.map(a => (log, a)))
  }

  def run() = {
    (1 to 100).map { index =>
      for {
        username    <- lift(recoverUsername(Random.nextInt()), Vector("Recovering username"))
        userDetails <- lift(getUserDetails(username), Vector("Recovering user details"))
        _           <- lift(validatePassword(userDetails.password), Vector("Validating password"))
        username    <- lift(recoverUsername(Random.nextInt()), Vector("Recovering username"))
        userDetails <- lift(getUserDetails(username), Vector("Recovering user details"))
        _           <- lift(validatePassword(userDetails.password), Vector("Validating password"))
        username    <- lift(recoverUsername(Random.nextInt()), Vector("Recovering username"))
        userDetails <- lift(getUserDetails(username), Vector("Recovering user details"))
        _           <- lift(validatePassword(userDetails.password), Vector("Validating password"))
      } yield index
    }.foreach {
      value: WriterT[FutureEitherT, Vector[String], Int] =>
        Await.result(value.run.value, Duration.Inf).foreach(result => println(s"[LOG ${result._2}] " + result._1))
    }
  }

  run()
}
