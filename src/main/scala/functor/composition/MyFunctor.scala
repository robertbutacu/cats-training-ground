package functor.composition

import scala.language.higherKinds

trait MyFunctor[F[_]] {
  def map[A, B](f: A => B)(fa: F[A]): F[B]
}


object MyFunctor {
  implicit class FunctorSyntax[A, F[_]](value: F[A]) {
    def map[B](f: A => B)(implicit F: MyFunctor[F]): F[B] = {
      F.map(f)(value)
    }
  }

  def apply[F[_]](implicit F: MyFunctor[F]): MyFunctor[F] = F
}