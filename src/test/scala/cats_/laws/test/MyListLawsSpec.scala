package cats_.laws.test

import cats.kernel.laws.discipline.SemigroupTests
import cats.laws.discipline.FunctorTests
import cats.tests.CatsSuite
import cats.syntax.semigroup._
import org.scalacheck.{Arbitrary, Gen}
// import cats.laws.discipline.FunctorTests

class MyListLawsSpec extends CatsSuite {

  implicit def arbFoo[A: Arbitrary]: Arbitrary[MyList[A]] =
    Arbitrary(Gen.frequency[MyList[A]]((20, Gen.const(Empty)), (80, for {
      e <- Arbitrary.arbitrary[A]
    } yield Head(e, arbFoo[A].arbitrary.sample.get)
    )))

  checkAll("MyList.FunctorLaws", FunctorTests[MyList].functor[Int, Int, Int])
  checkAll("MyList.SemigroupLaws", SemigroupTests[MyList[Int]].semigroup)

  val list1 = arbFoo[Int].arbitrary.sample.get
  val list2 = arbFoo[Int].arbitrary.sample.get

  //assert(list1.combine(list2) == list2.combine(list1))
  //checkAll
}
