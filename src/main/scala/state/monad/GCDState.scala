package state.monad

import cats.data.StateT
import cats.data.StateT._
import cats.{Eval, Monad}

import scala.language.higherKinds

object GCDState extends App {
  case class GCDState(x: Int, y: Int)

  def gcdProgram[F[_]](implicit M: Monad[F]): StateT[F, GCDState, Int] = {
    def recursiveMaybe(shouldStop: Boolean): StateT[F, GCDState, Int] = {
        if(shouldStop) {
          println("Reached end")
          StateT[F, GCDState, Int](gcd => M.pure(gcd, gcd.x))
        }
        else {
          for {
            _       <- modify[F, GCDState](gcd => if(gcd.x > gcd.y) GCDState(gcd.x - gcd.y, gcd.y) else GCDState(gcd.x, gcd.y - gcd.x))
            result  <- gcdProgram
          } yield result
        }
    }

    for {
      curr           <- get[F, GCDState]
      _              =  println("Current state is: " + curr)
      shouldStop     <- inspect[F, GCDState, Boolean](gcd => if(Math.abs(gcd.x - gcd.y) == 0) true else false)
      result         <- recursiveMaybe(shouldStop)
    } yield result
  }

  println(gcdProgram[Eval].run(GCDState(50, 75)).value)
}
