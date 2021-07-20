package com.iamsmkr.primegenerator

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl._
import com.iamsmkr.primecommon.ServerConfig
import com.iamsmkr.primegenerator.grpc._
import org.slf4j.Logger

import scala.concurrent._
import scala.util.{Failure, Success}

class PrimeGeneratorServer(sys: ActorSystem[_]) {

  def run(): Future[Http.ServerBinding] = {
    implicit val system: ActorSystem[_] = sys
    implicit val ec: ExecutionContext = system.executionContext

    val log: Logger = system.log

    val service: HttpRequest => Future[HttpResponse] = PrimeGeneratorServiceHandler(PrimeGeneratorServiceImpl(system.log))

    val config = ServerConfig("generator.server")

    val serverBindings = Http().newServerAt(config.interface, config.port).bind(service)

    serverBindings.onComplete {
      case Success(r) => log.info("Bound: {}", r.localAddress)
      case Failure(e) => log.error("Failed to bind. Shutting down", e)
        system.terminate()
    }

    serverBindings
  }
}

object PrimeGeneratorServer {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem[Nothing](Behaviors.ignore, "PrimeGeneratorServer")
    new PrimeGeneratorServer(system).run()
  }
}
