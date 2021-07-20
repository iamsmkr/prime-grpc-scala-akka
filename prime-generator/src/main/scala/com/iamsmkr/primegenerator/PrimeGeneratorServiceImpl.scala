package com.iamsmkr.primegenerator

import akka.NotUsed

import akka.event.LoggingAdapter
import akka.stream.scaladsl.Source
import com.iamsmkr.primegenerator.grpc._

class PrimeGeneratorServiceImpl(log: LoggingAdapter) extends PrimeGeneratorService {

  override def getPrimeNumbers(req: GetPrimeNumbersRequest): Source[GetPrimeNumbersReply, NotUsed] = {
    log.info("Received getPrimeNumbers request {} at prime number generator", req)

    Source(PrimeGenerator.getPrimeNumbers)
      .takeWhile(_ <= req.number)
      .map(n => GetPrimeNumbersReply(n))
  }
}

object PrimeGeneratorServiceImpl {
  def apply(log: LoggingAdapter): PrimeGeneratorServiceImpl = new PrimeGeneratorServiceImpl(log)
}
