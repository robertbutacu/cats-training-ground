/*
package capgemini.workshops

import scala.language.higherKinds

object Test2 {
  trait Option[A]

  case class Some[A](value: A) extends Option[A]
  case class None[A]()         extends Option[A]


  trait MyMonad[F[_]] {
    def flatMap[A, B](fa: F[A])(func: A => F[B]): F[B]
    def wrap[A](a: A): F[A]
  }

  def optionMonad = new MyMonad[Option] {
    override def flatMap[A, B](fa: Option[A])(func: A => Option[B]): Option[B] = {
      fa match {
        case None()     => None()
        case Some(a: A) => func(a)
      }
    }

    override def wrap[A](a: A): Option[A] = Option(a)
  }

  type MyEither[A] = Either[Throwable, A]

  def eitherMonad = new MyMonad[MyEither] {
    override def flatMap[A, B](fa: MyEither[A])(func: A => MyEither[B]): MyEither[B] = {
      fa match {
        case Left(ex)     => Left(ex)
        case Right(value) => func(value)
      }

    }

    override def wrap[A](a: A): MyEither[A] = Right(a)
  }
}
*/
