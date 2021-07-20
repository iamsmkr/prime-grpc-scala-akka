package com.iamsmkr.primeproxy

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import org.scalatest.wordspec.AnyWordSpec
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.iamsmkr.primeroxy.PrimeRoutes.Responses._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor}

class IntegrationTestingSpec extends AnyWordSpec {

  import IntegrationTestingSpec._

  "PrimeProxyServer" when {
    "given a number" should {
      "return all prime numbers up until that number" in {
        assert(responseForUri("/prime/23").equals("2,3,5,7,11,13,17,19,23"))
        assert(responseForUri("/prime/23/seq").equals("2,3,5,7,11,13,17,19,23"))
        assert(responseForUri("/prime/2/csv-stream").equals("2\n"))
        assert(responseForUri("/prime/2/sse").equals("data:2\n\n"))
      }
    }

    "given a number less than 2" should {
      "present user with valid numbers acceptable by the endpoint" in {
        assert(responseForUri("/prime/1").equals(INVALID_NUMBER_ARGUMENT))
      }
    }

    "given NaN" should {
      "present user with valid numbers acceptable by the endpoint" in {
        assert(responseForUri("/prime/xyz").equals(INVALID_NUMBER_ARGUMENT))
      }
    }

    "reached at URI other than /prime/:number" should {
      "redirects user to a valid URI" in {
        assert(responseForUri("/iamsmkr").equals(RESOURCE_NOT_FOUND))
      }
    }

  }

}

object IntegrationTestingSpec {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val baseUrl =
    Uri.from(
      scheme = "http",
      host = sys.env.getOrElse("PRIME_PROXY_INTERFACE", "localhost"),
      port = sys.env.getOrElse("PRIME_PROXY_PORT", "80").toInt
    )

  def responseForUri(uri: String): String = {
    val f = for {
      r <- Http().singleRequest(HttpRequest(uri = baseUrl.withPath(Path.apply(uri))))
      ur <- Unmarshal(r).to[String]
    } yield ur

    Await.result(f, 15.seconds)
  }
}
