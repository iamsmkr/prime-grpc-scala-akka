package com.iamsmkr.primeroxy

import com.iamsmkr.primegenerator.grpc._
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent._
import scala.util.{Failure, Success}
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent

import scala.concurrent.duration.DurationInt

object PrimeProxyService {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("PrimeProxyService")
    implicit val mat: Materializer = Materializer(system)
    implicit val ec: ExecutionContext = system.dispatcher
    val log: LoggingAdapter = system.log

    val settings = GrpcClientSettings.fromConfig("prime.PrimeGeneratorService")
    val client = PrimeGeneratorServiceClient(settings)

    val MAX_ALLOWED_SIZE = 10000

    val route =
      path("prime" / Segment) { number =>
        get {
          log.info("prime number request")

          val f = client.getPrimeNumbers(GetPrimeNumbersRequest(number.toLong))
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
      } ~ path("prime" / Segment / "sse") { number =>
        get {
          log.info("prime number request")

          complete {
            client.getPrimeNumbers(GetPrimeNumbersRequest(number.toLong))
              .map(_.primeNumber)
              .map(n => ServerSentEvent(n.toString))
              .keepAlive(1.second, () => ServerSentEvent.heartbeat)
          }
        }
      }

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bindFlow(route)
    bindingFuture.onComplete {
      case Success(sb) =>
        log.info("Bound: {}", sb)
      case Failure(t) =>
        log.error(t, "Failed to bind. Shutting down")
        system.terminate()
    }

  }
}
