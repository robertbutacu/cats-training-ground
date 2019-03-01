package applicatives

import cats.Applicative

object Applicatives extends App {
  implicit def optionApplicative: Applicative[Option] = new Applicative[Option] {
    override def pure[A](x: A): Option[A] = Option(x)

    override def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = {
      ff.flatMap(f => fa.map(a => f(a)))
    }
  }
}
