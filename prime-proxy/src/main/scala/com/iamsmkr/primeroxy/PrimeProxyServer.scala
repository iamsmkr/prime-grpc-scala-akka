package com.iamsmkr.primeroxy

import com.iamsmkr.primegenerator.grpc._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import com.iamsmkr.primecommon.ServerConfig
import org.slf4j.Logger

import scala.concurrent._
import scala.util.{Failure, Success}

class PrimeProxyServer(sys: ActorSystem[_]) {

  def run(): Future[Http.ServerBinding] = {
    implicit val system: ActorSystem[_] = sys
    implicit val ec: ExecutionContext = system.executionContext

    val log: Logger = system.log

    val settings = GrpcClientSettings.fromConfig("prime.PrimeGeneratorService")

    val client = PrimeGeneratorServiceClient(settings)

    val primeRoutes = PrimeRoutes(log, client).routes

    val config = ServerConfig("proxy.server")

    val serverBindings = Http().newServerAt(config.interface, config.port).bind(primeRoutes)

    serverBindings.onComplete {
      case Success(r) => log.info("Bound: {}", r.localAddress)
      case Failure(e) => log.error("Failed to bind. Shutting down", e)
        system.terminate()
    }

    serverBindings
  }
}

object PrimeProxyServer {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem[Nothing](Behaviors.ignore, "PrimeProxyService")
    new PrimeProxyServer(system).run()
  }
}
