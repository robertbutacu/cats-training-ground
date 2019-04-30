package workshop

import cats.data.OptionT
import cats.implicits._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.higherKinds

object SecondPart extends App {
  def futx: Future[Option[Int]] = Future.successful(Option(2))
  def futy: Future[Option[Int]] = Future.successful(Option(1))

/*
  def version1 = futx.flatMap { maybeX =>
    Future.sequence (
      maybeX.map(x => futy.map(maybeY => maybeY.map(y => y + x)))
      ) // ???
  }
*/

  def version2: Future[Option[Int]] = futx.flatMap {
    case None    => Future.successful(None)
    case Some(v) => futy.map(o => o.map(x => x + v))
  }

  def version3: Future[Option[Int]] = {
    for {
      maybeX <- futx
      maybeY <- futy
    } yield maybeX.flatMap(x => maybeY.map(_ + x))
  }

  def version4: Future[Option[Int]] =
    futx.flatMap { maybeX => maybeX.fold(Future.successful[Option[Int]](None))(x => futy.map(maybeY => maybeY.map(y => y + x)))}

  def optionTVersion: Future[Option[Int]] = {
    val result = for {
      x <- OptionT(futx)
      y <- OptionT(futy)
    } yield x + y

    result.value
  }

  println(optionTVersion)

  type Result[A] = OptionT[Future, A]

  case class StockItem(name: String)

  def findStockItem(name: String)       : Result[StockItem] = StockItem("Fruit").pure[Result]
  def findStockLevel(item: StockItem)   : Result[Int]       = 10.pure[Result]
  def findStockLevelByName(name: String): Result[Int]       = {
    for {
      item  <- findStockItem(name)
      level <- findStockLevel(item)
    } yield level
  }

  println(Await.result(findStockLevelByName("Washing machine").value, Duration.fromNanos(5000)))
}
