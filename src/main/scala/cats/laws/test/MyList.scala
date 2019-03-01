package cats.laws.test

import cats.{Eq, Functor}
import cats.kernel.Semigroup

sealed trait MyList[+A]

object MyList {
  implicit def listSemigroup[A]: Semigroup[MyList[A]] = new Semigroup[MyList[A]] {
    override def combine(x: MyList[A], y: MyList[A]): MyList[A] = {
      x match {
        case _: Empty.type => y
        case h: Head[A]  => Head(h.head, combine(h.tail, y))
      }
    }
  }

  implicit def listFunctor: Functor[MyList] = new Functor[MyList] {
    override def map[A, B](fa: MyList[A])(f: A => B): MyList[B] = fa match {
      case _: Empty.type => Empty
      case h: Head[A]  => Head(f(h.head), map(h.tail)(f))
    }
  }

  implicit def eqList[A: Eq]: Eq[MyList[A]] = Eq.fromUniversalEquals
}

case object Empty                        extends MyList[Nothing]
case class Head[A](head: A, tail: MyList[A]) extends MyList[A]
