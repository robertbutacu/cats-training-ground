package state.monad

import java.util.UUID

import cats.data.{IndexedStateT, State}
import State.{get, _}
import cats.Eval
import cats.instances.list.catsKernelStdMonoidForList

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

/*
  println(result.value._2)
*/

  RepositoryExample.program.map(p => p.runEmpty.value).foreach(r => println(r._1))

  object RepositoryExample {
    def insert(record: Record): State[List[Record], Unit] = for {
      _          <- modify[List[Record]](records => records :+ record)
    } yield ()


    def remove[A](record: Record): State[List[Record], Unit] = {
      for {
        _          <- modify[List[Record]](records => records.filterNot(_ == record))
      } yield ()
    }

    def update(old: Record, updated: Record): State[List[Record], Unit] = {
      for {
        _ <- modify[List[Record]](records => records.filterNot(_ == old) :+ updated)
      } yield ()
    }

    def programExample(records: List[Record], toDelete: Record, toUpdate: (Record, Record)): State[List[Record], List[Record]] = for {
      _               <- set(records)
      _               <- remove(toDelete)
      _               <- update(toUpdate._1, toUpdate._2)
      _               <- remove(toDelete)
      finalRepository <- get[List[Record]]
    } yield finalRepository

    val recordsGenerated: List[Record] = (0 to 1).map(_ => Record(name = Random.nextString(5))).toList :+ Record(name = "TEST") :+ Record(name = "other")
    val toDelete: Option[Record] = recordsGenerated.find(_.name == "TEST")
    val toUpdate: Option[Record] = recordsGenerated.find(_.name == "other")
    val updated = Record(name = "UPDATED")

    val program: Option[State[List[Record], List[Record]]] = for {
      d <- toDelete
      u <- toUpdate
    } yield programExample(recordsGenerated, d, (u, updated))
  }
}
