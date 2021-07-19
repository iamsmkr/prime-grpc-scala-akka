package com.example.helloworld

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl.Sink
import com.iamsmkr.primegenerator.grpc.{GetPrimeNumbersRequest, PrimeGeneratorServiceClient}

import scala.concurrent._
import scala.util._

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
