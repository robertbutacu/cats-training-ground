package capgemini.workshops

import capgemini.workshops.MonadExercises.{MyError, program}
import cats.effect.IO

import scala.util.Try

object MonadRunners extends App {
  import cats.instances.all._

  type EitherException[A] = Either[Throwable, A]

  println(program[EitherException]())
  println(program[Try]())
  println(program[IO]().unsafeToFuture())
  println(program[IO]().unsafeRunSync())
}
