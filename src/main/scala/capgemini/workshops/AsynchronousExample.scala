package capgemini.workshops

import java.time.Instant

import cats.effect.IO
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object AsynchronousExample extends App {
  def futurePrint: Future[Unit] = Future { println("[FUTURE] The time is: " + Instant.now)}
  def ioPrint: IO[Unit] = IO { println("[IO] The time is: " + Instant.now)}

  def runFutures(): Unit = {
    val print1 = futurePrint

    Thread.sleep(2000)

    val print2 = futurePrint

    Thread.sleep(2000)

    for {
      _ <- print1
      _ <- print2
    } yield ()
  }

  def runIOs(): Unit = {
    val io1 = ioPrint

    Thread.sleep(2000)

    val io2 = ioPrint

    Thread.sleep(2000)

    (for {
      _ <- io1
      _ <- io2
    } yield ()).unsafeRunSync()
  }

  runFutures()
  Thread.sleep(3000)
  runIOs()
}
