package workshop

import cats.{MonadError, Traverse}
import cats.syntax.all._
import cats.instances.list._

class PersonService[F[_]](database: PersonDatabase[F])(implicit M: MonadError[F, Throwable]) {
  // 1. Write code to:
  //     - Determine whether one person knows another through a chain of intermediate friends.

  def isFriends(from: String, to: String): F[Boolean] = {
    def go(enqueue: List[Person], visited: List[Person]): F[Boolean] = {
      enqueue.headOption.fold(M.pure(false)) {
        p =>
          if(p.name == to) M.pure(true)
          else {
            for {
              friends <- Traverse[List]
                .traverse(p.friends)(n => database.find(n))
                .map(people => people.filterNot(visited.contains))
              result  <- go(friends, visited :+ p)
            } yield result
          }
      }
    }

    val person = database.find(from)

    person.flatMap(p => go(List(p), List.empty))
  }

  //
  //     - Given a key person P and a number N,
  //       find a list of N transitive friends of P to invite to a dinner party.

     def nTransitiveFriends(name: String, n: Int): F[Set[Person]] = {
        // to make this tailrec, use a queue for people to look up
        def go(queue: List[Person], friends: Set[Person], currentN: Int): F[Set[Person]] = {
          if(currentN == n) M.pure(friends)
          else {
            queue.headOption.fold(M.pure(friends)) {
              person: Person =>
                for {
                  updatedFriends <- Traverse[List].traverse(person.friends)(p => database.find(p))
                  filtered        = updatedFriends.filterNot(friends.contains)
                  result         <- go(queue.tail ::: filtered, friends + person, currentN + 1)
                } yield result
            }
          }
        }

       database.find(name).flatMap(p => go(List(p), Set.empty, currentN = 0))
      }

  def servingPlan(people: Set[Person]): F[Set[String]] = {
    M.pure(people.map(_.favoriteFood))
  }
}
