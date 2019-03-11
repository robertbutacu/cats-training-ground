package state.monad

import java.util.UUID

import cats.data.{IndexedStateT, State}
import State._
import cats.Eval

import scala.util.Random

object StateRepository  extends App {
  case class Record(id: String = UUID.randomUUID().toString, name: String)

  def program(): IndexedStateT[Eval, List[Record], List[Record], (List[Record], List[Record], Option[Record], List[Record])] = for {
    records        <- get[List[Record]]
    _              <- modify[List[Record]](records => records.filter(_.name != "TEST"))
    updatedRecords <- get[List[Record]]
    foundRecord    <- inspect[List[Record], Option[Record]](records => records.find(_.name == "other"))
    _              <- modify[List[Record]](_ => List.empty)
    _              <- set[List[Record]](List(Record(name = "Finished"), Record(name = "finished2")))
    doneRecords    <- get[List[Record]]
  } yield (records, updatedRecords, foundRecord, doneRecords)

  val recordsGenerated = (0 to 10).map(_ => Record(name = Random.nextString(5))).toList :+ Record(name = "TEST") :+ Record(name = "other")

  val result: Eval[(List[Record], (List[Record], List[Record], Option[Record], List[Record]))] = program().run(recordsGenerated)

  println(result.value._2)
}
