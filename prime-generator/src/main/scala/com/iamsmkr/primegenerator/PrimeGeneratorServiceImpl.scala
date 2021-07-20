package com.iamsmkr.primegenerator

import akka.NotUsed
import akka.event.LoggingAdapter
import akka.grpc.GrpcServiceException
import akka.stream.scaladsl.Source
import com.iamsmkr.primecommon._
import com.iamsmkr.primegenerator.grpc._
import io.grpc.Status

class PrimeGeneratorServiceImpl(log: LoggingAdapter) extends PrimeGeneratorService {

  override def getPrimeNumbers(req: GetPrimeNumbersRequest): Source[GetPrimeNumbersReply, NotUsed] = {
    log.info("Received getPrimeNumbers request {} at prime number generator", req)

    val res = validateNumberArg(req.number)

    if (res.isDefined)
      Source.failed(new GrpcServiceException(Status.INVALID_ARGUMENT.withDescription(res.get.msg)))
    else
      Source(PrimeGenerator.getPrimeNumbers)
        .takeWhile(_ <= req.number)
        .map(n => GetPrimeNumbersReply(n))
  }
}

object PrimeGeneratorServiceImpl {
  def apply(log: LoggingAdapter): PrimeGeneratorServiceImpl = new PrimeGeneratorServiceImpl(log)
}
