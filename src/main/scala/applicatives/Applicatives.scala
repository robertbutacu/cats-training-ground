package applicatives

import cats.Applicative
import cats.syntax.applicative._
import cats.instances.option._
object Applicatives extends App {
  implicit def optionApplicative: Applicative[Option] = new Applicative[Option] {
    override def pure[A](x: A): Option[A] = Option(x)

    override def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = {
      ???
    }
  }

  def func(a: Int, b: String): Int = a

  val x = Option(5)
  val y = Option("asda")

  val applicativeExample: Option[String => Int] = x.map{ i => s: String => func(i, s)}

  // the way I see it, Applicative is quite close to being a Monad, but not quite so
  // it doesn't have the ability to deal with some extra computations,
  // but it does have the ability to combine multiple effects to get a common result

  def traverseOption[A, B](as: List[A])(f: A => Option[B]): Option[List[B]] = {
    as.foldLeft(Option(List.empty[B])){(acc: Option[List[B]], curr: A) =>
      val res: Option[B] = f(curr)

      Applicative[Option].map2(acc, res)(_ :+ _)
    }
  }

  def map2[A, B, Z](fa: Option[A], fb: Option[B])(f: (A, B) => Z)(implicit A: Applicative[Option]): Option[Z] = {
    A.map(product(fa, fb))(f.tupled)
  }

  def product[A, B](fa: Option[A], fb: Option[B])(implicit A: Applicative[Option]): Option[(A, B)] = {
    A.ap(fa.map{a => b: B => (a, b)})(fb)
  }

  println(product(Some(2), Some(3)))

  def traverse[A, B](list: List[A])(f: A => Option[B]): Option[List[B]] = {
    ???
  }

  println(traverseOption(List.empty[Int]){i: Int => Some(i)})
}

