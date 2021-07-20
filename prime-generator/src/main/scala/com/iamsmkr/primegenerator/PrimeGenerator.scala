package com.iamsmkr.primegenerator

object PrimeGenerator {
  val primes: Stream[Int] = 2 #:: Stream.from(3).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }

  def getPrimeNumbers: Stream[Int] = primes
}
