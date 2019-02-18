package capgemini.workshops

object SemigroupApp extends App {
  trait Semigroup[A] {
    def combine(first: A, second: A): A
  }

  object Semigroup {

    implicit class SemigroupOps[A](a: A) {
      def |+|(other: A)(implicit M: Semigroup[A]): A = M.combine(a, other)
    }

    implicit def additionalSemigroup: Semigroup[Int] = new Semigroup[Int] {
      override def combine(first: Int, second: Int): Int = first + second
    }

    implicit def stringSemigroup = new Semigroup[String] {
      override def combine(first: String, second: String): String = first ++ second
    }
  }

  def add[A](a: A, b: A)(implicit semigroup: Semigroup[A]): A = semigroup.combine(a, b)

}
