package functor.composition

import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](f: A => B)(fa: F[A]): F[B]
}


object Functor {
  implicit class FunctorSyntax[A, F[_]](value: F[A]) {
    def map[B](f: A => B)(implicit F: Functor[F]): F[B] = {
      F.map(f)(value)
    }
  }

  def apply[F[_]](implicit F: Functor[F]): Functor[F] = F
}