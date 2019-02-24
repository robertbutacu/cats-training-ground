package io.monad.scalaz

import scalaz.zio._
import scalaz.zio.console._

object IoMonadApp extends App {
  trait MyException extends Exception

  case object Unlucky extends MyException
  case object BadDay  extends MyException
  case object Random  extends MyException
  case object NumberFormatException extends MyException

  def run(args: List[String]) : IO[Nothing, ExitStatus] = {

    {for {
      input      <- putStrLn("Give me a number") *> getStrLn
      number     <- mapNumber(input)
      x          <- someComputation(number)
      otherInput <- putStrLn("Give me another number") *> getStrLn.map(_.toInt)
      y          <- someOtherComputation(otherInput)
      _          <- putStr((x + y).toString)
    } yield ()}.run.redeemPure(_ => ExitStatus.ExitNow(1), _ => ExitStatus.ExitNow(2))
  }

  def someComputation(input: Int): IO[MyException, Int] = {
    if(input % 2 == 0) IO.now(input * 10)
    else               IO.fail(BadDay)
  }

  def mapNumber(s: String): IO[MyException, Int] = {
    if(s.forall(_.isDigit)) IO.now(s.toInt)
    else IO.fail(NumberFormatException)
  }

  def someOtherComputation(max: Int): IO[MyException, Int] = IO.sync(scala.util.Random.nextInt(max))
}
