package com.example.helloworld

import akka.NotUsed

import akka.event.LoggingAdapter
import akka.stream.scaladsl.Source
import com.iamsmkr.primegenerator.grpc.{GetPrimeNumbersReply, GetPrimeNumbersRequest, PrimeGeneratorService}

class PrimeGeneratorServiceImpl(log: LoggingAdapter) extends PrimeGeneratorService {
  override def getPrimeNumbers(req: GetPrimeNumbersRequest): Source[GetPrimeNumbersReply, NotUsed] = {
    log.info("Received getPrimeNumbers request {}", req)

    def sieve(s: Stream[Int]): Stream[Int] = s.head #:: sieve(s.tail.filter(_ % s.head != 0))

    Source(sieve(Stream.from(2)))
      .takeWhile(_ <= req.number)
      .map(n => GetPrimeNumbersReply(n))
  }
}
