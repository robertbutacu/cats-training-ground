package capgemini.workshops

object MonoidApp {
  trait Monoid[A] {
    def combine(a: A, b: A): A
    def identity: A // combine(identity, other) == other
  }

  object Monoid {
    implicit class MonoidOps[A](a: A)(implicit M: Monoid[A]) {
      def combine(other: A) = M.combine(a, other)

      def isIdentity: Boolean = M.identity == a
    }

    implicit val additionMonoid: Monoid[Int] = new Monoid[Int] {
      override def combine(a: Int, b: Int): Int = a + b

      override def identity: Int = 0
    }
  }
}
