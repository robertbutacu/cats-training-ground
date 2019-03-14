package functor.composition

import scala.concurrent.Future
import scala.language.higherKinds



object FunctorCats extends App {
  trait MyFunctor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  object MyFunctor {

    object ops {

      implicit class ExtraString(value: String) {
        def myStringOperation: String = ???
      }

      implicit class EffectOps[F[_], A](value: F[A]) {
        def map[B](f: A => B)(implicit F: MyFunctor[F]): F[B] =
          F.map(value)(f)
      }
    }


    implicit val optionFunctor: MyFunctor[Option] = new MyFunctor[Option] {
      override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
    }
  }

  trait MyList[A]
  case class Empty[A]() extends MyList[A]
  case class Head[A](elem: A, tail: MyList[A]) extends MyList[A]

  implicit val myListFunctor: MyFunctor[MyList] = new MyFunctor[MyList] {
    override def map[A, B](fa: MyList[A])(f: A => B): MyList[B] = {
      fa match {
        case e: Empty[A] => Empty[B]
        case h: Head[A] => Head(f(h.elem), map(h.tail)(f))
      }
    }
  }

  trait Either[E, R]

  case class Right[E,R](value: R) extends Either[E,R]
  case class Left[E,R](err: E) extends Either[E,R]

  def divide(a: Int, b: Int): Either[Exception, Int] = {
    if(b == 0) Left(new IllegalArgumentException("Division by zero"))
    else Right(a / b)
  }
}
