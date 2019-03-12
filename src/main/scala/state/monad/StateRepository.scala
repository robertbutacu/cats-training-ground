package state.monad

import java.util.UUID

import cats.data.{IndexedStateT, State, StateT}
import State._
import cats.effect.IO
import cats.{Applicative, Eval, Id, Monad}
import cats.instances.list.catsKernelStdMonoidForList

import scala.language.higherKinds
import scala.util.{Random, Try}

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
  ProgramRun.runProgramInTry()
  ProgramRun.runProgramInId()
  ProgramRun.runProgramInFuture()

  object RepositoryExample {
    import StateT._

    def insert[F[_]](record: Record)(implicit A: Applicative[F]): StateT[F, List[Record], Unit] = modify {
      records => records :+ record
    }

    def remove[F[_]](record: Record)(implicit A: Applicative[F]): StateT[F, List[Record], Unit] = modify {
      records => records.filterNot(_ == record)
    }

    def update[F[_]](old: Record, updated: Record)(implicit A: Applicative[F]): StateT[F, List[Record], Unit] = modify {
      records => records.filterNot(_ == old) :+ updated
    }

    def find[F[_]](record: Record)(implicit A: Applicative[F]): StateT[F, List[Record], Option[Record]] = inspect {
      records => records.find(_ == record)
    }

    def programExample[F[_]](records: List[Record], toDelete: Record, toUpdate: (Record, Record))(implicit M: Monad[F]): StateT[F, List[Record], List[Record]] = for {
      _               <- set(records)
      _               <- get[F, List[Record]]
      _               =  println("Inside the program " + records)
      _               <- remove(toDelete)
      _               <- update(toUpdate._1, toUpdate._2)
      _               <- remove(toDelete)
      finalRepository <- get[F, List[Record]]
    } yield finalRepository

    val recordsGenerated: List[Record] = (0 to 0).map(_ => Record(name = Random.nextString(5))).toList :+ Record(name = "TEST") :+ Record(name = "other")
    val toDelete: Option[Record] = recordsGenerated.find(_.name == "TEST")
    val toUpdate: Option[Record] = recordsGenerated.find(_.name == "other")
    val updated = Record(name = "UPDATED")

    def program[F[_]](implicit M: Monad[F]): Option[StateT[F, List[Record], List[Record]]] = for {
      d <- toDelete
      u <- toUpdate
    } yield programExample[F](recordsGenerated, d, (u, updated))
  }

  object ProgramRun {
    import cats.instances.try_._

    IndexedStateT
    def runProgramInTry(): Unit = RepositoryExample.program[Try](implicitly[Monad[Try]]).map(p => p.runEmpty).foreach(r => println(r + "\n\n"))
    def runProgramInId(): Unit = RepositoryExample.program[Id](implicitly[Monad[Id]]).map(p => p.runEmpty).foreach(r => println(r + "\n\n"))
    def runProgramInFuture(): Unit = RepositoryExample.program[IO](implicitly[Monad[IO]]).map(p => p.runEmpty.unsafeRunSync()).foreach(r => println(r + "\n\n"))
  }
}
