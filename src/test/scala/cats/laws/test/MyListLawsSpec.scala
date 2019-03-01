package cats.laws.test


import cats.kernel.laws.discipline.SemigroupTests
import cats.laws.discipline.FunctorTests
import cats.tests.CatsSuite
import org.scalacheck.{Arbitrary, Gen}
// import cats.laws.discipline.FunctorTests

class MyListLawsSpec extends CatsSuite {

  implicit def arbFoo[A: Arbitrary]: Arbitrary[MyList[A]] =
    Arbitrary(Gen.oneOf(Gen.const(Empty), for {
      e <- Arbitrary.arbitrary[A]
    } yield Head(e, Empty))
    )

  checkAll("MyList.FunctorLaws", FunctorTests[MyList].functor[Int, String, String])
  checkAll("MyList.FunctorLaws", SemigroupTests[MyList[Int]].semigroup)

  //checkAll
}
