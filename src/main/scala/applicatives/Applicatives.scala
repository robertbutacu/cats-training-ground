package applicatives

import cats.Applicative

object Applicatives extends App {
  implicit def optionApplicative = new Applicative[Option] {
    override def pure[A](x: A): Option[A] = Option(x)

    override def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = {
      fa.flatMap(a => ff.map(f => f(a)))
    }
  }
}
