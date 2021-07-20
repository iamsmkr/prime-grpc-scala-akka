package com.iamsmkr.primeproxy

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.scaladsl.Source
import com.iamsmkr.primegenerator.grpc._
import com.iamsmkr.primeroxy.PrimeRoutes
import com.iamsmkr.primeroxy.PrimeRoutes.Responses._
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt

class PrimeProxySpec extends AnyWordSpecLike with should.Matchers with ScalatestRouteTest with MockFactory {

  val testKit = ActorTestKit()

  val sys = testKit.system

  override def afterAll(): Unit = testKit.shutdownTestKit()

  implicit val timeout = RouteTestTimeout(5.seconds)

  val clientStub: PrimeGeneratorService = stub[PrimeGeneratorService]

  private def getResponse(ls: List[Int]) = Source(ls.map(value => new GetPrimeNumbersReply(value)))

  (clientStub.getPrimeNumbers _).when(GetPrimeNumbersRequest(23L)).returning(getResponse(List(2, 3, 5, 7, 11, 13, 17, 19, 23)))
  (clientStub.getPrimeNumbers _).when(GetPrimeNumbersRequest(2L)).returning(getResponse(List(2)))

  val routes = PrimeRoutes(sys.log, clientStub).routes

  "PrimeProxyServer" when {
    "given a number" should {
      "return all prime numbers up until that number" in {
        Get("/prime/23") ~> routes ~> check {
          responseAs[String] shouldEqual "2,3,5,7,11,13,17,19,23"
        }

        Get("/prime/23/seq") ~> routes ~> check {
          responseAs[String] shouldEqual "2,3,5,7,11,13,17,19,23"
        }

        Get("/prime/2/csv-stream") ~> routes ~> check {
          responseAs[String] shouldEqual "2\n"
        }

        Get("/prime/2/sse") ~> routes ~> check {
          responseAs[String] shouldEqual "data:2\n\n"
        }
      }
    }

    "given a number less than 2" should {
      "present user with valid numbers acceptable by the endpoint" in {
        Get("/prime/1") ~> routes ~> check {
          responseAs[String] shouldEqual INVALID_NUMBER_ARGUMENT
        }
      }
    }

    "given NaN" should {
      "present user with valid numbers acceptable by the endpoint" in {
        Get("/prime/xyz") ~> routes ~> check {
          responseAs[String] shouldEqual INVALID_NUMBER_ARGUMENT
        }
      }
    }

    "reached at URI other than /prime/:number" should {
      "redirects user to a valid URI" in {
        Get("/iamsmkr") ~> routes ~> check {
          responseAs[String] shouldEqual RESOURCE_NOT_FOUND
        }
      }
    }

  }
}
