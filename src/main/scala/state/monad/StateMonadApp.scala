package state.monad

import cats.Monoid
import cats.data.State

import scala.util.Random

object StateMonadApp extends App {
  sealed trait Chess

  case object Normal    extends Chess
  case object Check     extends Chess
  case object Checkmate extends Chess
  case object Stalemate extends Chess

  val randomNextState = Map(0 -> Normal, 1 -> Check, 2 -> Checkmate, 3 -> Stalemate)

  def nextState: State[Chess, Unit] = State { state =>
    println("Current state is: " + state)
    state match {
      case Normal => (randomNextState(Random.nextInt(4)), ())
      case Check => (randomNextState(Random.nextInt(4)), ())
      case Checkmate => (Checkmate, ())
      case Stalemate => (Stalemate, ())
    }
  }

  def play(): State[Chess, Unit] = {
    for {
      _ <- State.get
      _ <- nextState
      _ <- nextState
      _ <- nextState
    }  yield State.get
  }

  println(play().run(Checkmate).value)
}
