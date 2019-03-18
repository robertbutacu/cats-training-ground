package capgemini.workshops

import capgemini.workshops.MonadExercises.{MyError, UserDetails, program}
import cats.effect.IO

import scala.util.Try

object MonadRunners extends App {
  import cats.instances.all._

  type EitherException[A] = Either[Throwable, A]
  case class ProgramFailed(err: String)

  println((0 to 5).map(id => program[EitherException](id)).toList)
  println((0 to 5).map(id => program[Try](id)).toList)
  println((0 to 5).map(id => program[IO](id).redeem(e => ProgramFailed(e.toString), _: UserDetails => _).unsafeRunSync()).toList)
  println((0 to 5).map(id => program[IO](id).unsafeToFuture()).toList)
}
