package com.iamsmkr.primegenerator

object PrimeGenerator {

  private def sieve(s: Stream[Int]): Stream[Int] = s.head #:: sieve(s.tail.filter(_ % s.head != 0))

  def getPrimeNumbers: Stream[Int] = sieve(Stream.from(2))
}
