package com.iamsmkr.primeroxy

import com.iamsmkr.primegenerator.grpc._
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import com.iamsmkr.primecommon.ServerConfig

import scala.concurrent._
import scala.util.{Failure, Success}

object PrimeProxyServer {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("PrimeProxyService")
    implicit val ec: ExecutionContext = system.dispatcher

    val log: LoggingAdapter = system.log

    val settings = GrpcClientSettings.fromConfig("prime.PrimeGeneratorService")
    val client = PrimeGeneratorServiceClient(settings)

    val primeRoutes = PrimeRoutes(log, client).routes

    val config = ServerConfig("proxy.server")

    Http().newServerAt(config.interface, config.port)
      .bindFlow(primeRoutes)
      .onComplete {
        case Success(r) => log.info("Bound: {}", r.localAddress)
        case Failure(e) => log.error(e, "Failed to bind. Shutting down")
          system.terminate()
      }

  }
}
