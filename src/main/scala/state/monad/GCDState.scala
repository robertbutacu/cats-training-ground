package state.monad

import cats.data.State

object GCDState extends App {
  case class GCDState(x: Int, y: Int)

  trait CurrentState {
    def currentStep: Int

    def increment(curr: CurrentState): CurrentState = {
      curr match {
        case Continue(x) => Continue(x + 1)
        case Stop(x)     => Stop(x + 1)
      }
    }
  }

  case class Continue(currentStep: Int) extends CurrentState
  case class Stop(currentStep: Int)     extends CurrentState

  def analizeState(x: Int, y: Int): State[GCDState, CurrentState] = State { state =>
    val diff = x - y
    diff match {
      case below if diff < 0  => (GCDState(x, diff), Stop(0))
      case zero  if diff == 0 => (GCDState(x, y), Stop(0))
      case above if diff > 0  => (GCDState(diff, y), Stop(0))
    }
  }

/*
    def gcd(): State[GCDState, CurrentState] = {
/*    def status(currentState: CurrentState): State[GCDState, CurrentState] = currentState match {
      case Continue(_) => gcd()
      case Stop(_) => State.get
    }*/

    for {
      curr     <- State.get
      _        <- analizeState(curr.x, curr.y)
    } yield State.get
  }
*/

  //println(State.apply[GCDState, CurrentState](state => analizeState(state.x, state.y)).run(GCDState(675, 123)).value)
}
