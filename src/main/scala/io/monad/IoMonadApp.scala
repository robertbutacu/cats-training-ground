package io.monad

import scalaz.zio._
import scalaz.zio.console._
import java.io.IOException

object IoMonadApp extends App {
  trait MyException extends Exception

  case object Unlucky extends MyException
  case object BadDay  extends MyException
  case object Random  extends MyException

  def run(args: List[String]) : IO[Nothing, ExitStatus] = {

    {for {
      input      <- putStrLn("Give me a number") *> getStrLn.map(_.toInt)
      x          <- someComputation(input)
      otherInput <- putStrLn("Give me another number") *> getStrLn.map(_.toInt)
      y          <- someOtherComputation(otherInput)
      _          <- putStr((x + y).toString)
    } yield ()}.redeemPure(_ => ExitStatus.ExitNow(1), _ => ExitStatus.ExitNow(2))
  }

  def someComputation(input: Int): IO[MyException, Int] = {
    if(input % 2 == 0) IO.now(input * 10)
    else               IO.fail(BadDay)
  }

  def someOtherComputation(max: Int): IO[MyException, Int] = IO.sync(scala.util.Random.nextInt(max))
}
