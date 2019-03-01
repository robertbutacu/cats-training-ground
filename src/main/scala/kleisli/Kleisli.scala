package kleisli

import cats.{Functor, Monad}

import scala.language.higherKinds

case class Kleisli[F[_], Z, A](run: Z => F[A]) {
  def flatMap[B](f: A => Kleisli[F, Z, B])(implicit M: Monad[F]): Kleisli[F, Z, B] = {
    Kleisli(z => M.flatMap(run(z))(a => f(a).run(z)))
  }

  def map[B](f: A => B)(implicit F: Functor[F]): Kleisli[F, Z, B] = {
    Kleisli(z => F.map(run(z))(a => f(a)))
  }

  def liftContext[ZZ](f: ZZ => Z): Kleisli[F, ZZ, A] = Kleisli(f.andThen(run))
}