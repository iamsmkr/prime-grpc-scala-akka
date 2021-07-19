package com.example.helloworld

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl._
import com.iamsmkr.primegenerator.grpc.PrimeGeneratorServiceHandler

import scala.concurrent.{ExecutionContext, Future}

object PrimeGeneratorServer {

  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("PrimeGeneratorServer")
    new PrimeGeneratorServer(system).run()
  }
}

class PrimeGeneratorServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem = system
    implicit val ec: ExecutionContext = sys.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      PrimeGeneratorServiceHandler(new PrimeGeneratorServiceImpl(system.log))

    val bound = Http().newServerAt("0.0.0.0", 8080).bind(service)

    bound.foreach { binding =>
      sys.log.info("gRPC server bound to: {}", binding.localAddress)
    }

    bound
  }
}
