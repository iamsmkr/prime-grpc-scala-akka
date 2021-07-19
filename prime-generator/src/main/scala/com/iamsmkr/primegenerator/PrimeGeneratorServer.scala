package com.iamsmkr.primegenerator

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl._
import com.iamsmkr.primegenerator.grpc._

import scala.concurrent._
import scala.util.{Failure, Success}

object PrimeGeneratorServer {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("PrimeGeneratorServer")
    implicit val ec: ExecutionContext = system.dispatcher

    val log: LoggingAdapter = system.log

    val service: HttpRequest => Future[HttpResponse] = PrimeGeneratorServiceHandler(new PrimeGeneratorServiceImpl(system.log))

    Http().newServerAt("0.0.0.0", 8080)
      .bind(service)
      .onComplete {
        case Success(r) => log.info("Bound: {}", r.localAddress)
        case Failure(e) => log.error(e, "Failed to bind. Shutting down")
          system.terminate()
      }
  }
}
