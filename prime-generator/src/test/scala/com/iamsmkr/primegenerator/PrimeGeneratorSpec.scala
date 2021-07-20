package com.iamsmkr.primegenerator

import org.scalatest.matchers.should
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PrimeGeneratorSpec extends AnyPropSpec with should.Matchers with ScalaCheckPropertyChecks {
  property("PrimeGeneratorService should return all prime numbers up until a given number") {
    val results =
      Table(
        ("number", "result"),
        (23, Stream(2, 3, 5, 7, 11, 13, 17, 19, 23)),
        (2, Stream(2)),
        (1, Stream.empty[Int]),
        (-1, Stream.empty[Int])
      )

    forAll(results) { (number, result) =>
      PrimeGenerator.getPrimeNumbers.takeWhile(_ <= number) should equal(result)
    }
  }
}
