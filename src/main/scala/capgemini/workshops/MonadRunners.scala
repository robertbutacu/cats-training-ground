package capgemini.workshops

import capgemini.workshops.MonadExercises.program

object MonadRunners extends App {
  import cats.instances.option._

  println(program[Option]())
}
