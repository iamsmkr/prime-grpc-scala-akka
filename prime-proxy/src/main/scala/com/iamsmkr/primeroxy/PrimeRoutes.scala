package com.iamsmkr.primeroxy

import com.iamsmkr.primegenerator.grpc._
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent._
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Route

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class PrimeRoutes(log: LoggingAdapter, client: PrimeGeneratorServiceClient)(implicit mat: Materializer, ec: ExecutionContext) {

  import PrimeRoutes._

  private val MAX_ALLOWED_SIZE = 10000

  val routes: Route =
    path("prime" / LongNumber) { number =>
      get {
        log.info(s"prime numbers up until number $number are requested")

        val res = validateNumber(number)

        if (res.isDefined) complete(BadRequest, res.get.msg)
        else {
          val f = client.getPrimeNumbers(GetPrimeNumbersRequest(number))
            .map(_.primeNumber)
            .take(MAX_ALLOWED_SIZE)
            .runWith(Sink.seq)
            .map(_.mkString(","))

          onComplete(f) {
            case Success(reply) => complete(reply)
            case Failure(t) =>
              log.error(t, "Request failed")
              complete(StatusCodes.InternalServerError, t.getMessage)
          }
        }

      }
    } ~ path("prime" / LongNumber / "sse") { number =>
      get {
        log.info(s"prime numbers up until number $number are requested as sse")

        val res = validateNumber(number)

        if (res.isDefined) complete(BadRequest, res.get.msg)
        else {
          complete {
            client.getPrimeNumbers(GetPrimeNumbersRequest(number))
              .map(_.primeNumber)
              .map(n => ServerSentEvent(n.toString))
              .keepAlive(1.second, () => ServerSentEvent.heartbeat)
          }
        }

      }
    }
}

object PrimeRoutes {
  def apply(log: LoggingAdapter, client: PrimeGeneratorServiceClient)(implicit mat: Materializer, ec: ExecutionContext): PrimeRoutes =
    new PrimeRoutes(log, client)(mat, ec)

  case class InvalidNumber(msg: String)

  def validateNumber(number: Long): Option[InvalidNumber] = {
    if (number < 2) Some(InvalidNumber("Please provide a number greater 1"))
    else None
  }
}
