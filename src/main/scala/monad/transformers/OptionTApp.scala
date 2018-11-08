package monad.transformers

import cats._
import cats.data._
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import ExecutionContext.Implicits.global

object OptionTApp extends App {
  def expensiveSuccessfulComputation1[A](input: A)(implicit s: Show[A], executionContext: ExecutionContext): Future[Option[String]] = Future.successful(Some(s.show(input)))
  def expensiveSuccessfulComputation2[A](input: A)(implicit s: Show[A], executionContext: ExecutionContext): Future[Option[String]] = Future.successful(Some(s.show(input)))
  def expensiveFailedComputation2[A](input: A)(implicit s: Show[A], executionContext: ExecutionContext): Future[Option[String]] = Future.successful(Some(s.show(input)))

  def someInt()(implicit s: Show[Int]): Option[String] = Option(s.show(Random.nextInt()))

  def doSomeWork(): OptionT[Future, String] = {
    for {
      a <- OptionT(expensiveSuccessfulComputation1(123))
      b <- OptionT(expensiveSuccessfulComputation2(456))
    } yield a + b
  }

  def includeSomeWork(): OptionT[Future, String] = {
    for {
      number <- OptionT(Future.successful(someInt()))
      otherNumber <- doSomeWork()
    } yield number + otherNumber
  }

  includeSomeWork().fold("FAILED")(s => s).foreach(println)

  //necessary to complete the future =>
  // basically, the Futures run on separate Threads - they dont get to finish before the program fininshes its execution
  // hence, without making the current thread sleep, the future wouldnt complete
  Thread.sleep(1000)
}
