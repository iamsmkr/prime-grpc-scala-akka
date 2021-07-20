package com.iamsmkr.primegenerator

import akka.actor.ActorSystem
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.grpc.GrpcServiceException
import akka.stream.scaladsl.Sink
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import akka.testkit._
import com.iamsmkr.primegenerator.grpc.GetPrimeNumbersRequest
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class PrimeGeneratorServiceSpec extends TestKit(ActorSystem("PrimeGeneratorServiceTest")) with AnyWordSpecLike
  with should.Matchers with ScalaCheckPropertyChecks with BeforeAndAfterAll {

  val testKit: ActorTestKit = ActorTestKit()

  val sys = testKit.system

  override def afterAll(): Unit = testKit.shutdownTestKit()

  implicit val ec: ExecutionContext = sys.executionContext

  "PrimeGeneratorService" when {
    "given a number" should {
      "return all prime numbers up until that number" in {
        val results =
          Table(
            ("number", "result"),
            (23, "2,3,5,7,11,13,17,19,23")
          )

        forAll(results) { (number, result) =>
          Await.result(
            PrimeGeneratorServiceImpl(sys.log)
              .getPrimeNumbers(GetPrimeNumbersRequest(number))
              .map(_.primeNumber)
              .runWith(Sink.seq)
              .map(_.mkString(",")),
            5.seconds
          ) should be(result)
        }
      }
    }

    "given a number less than 2" should {
      "fail with an exception" in {
        assertThrows[GrpcServiceException] {
          Await.result(
            PrimeGeneratorServiceImpl(sys.log).getPrimeNumbers(GetPrimeNumbersRequest(0)).runWith(Sink.ignore),
            5.seconds
          )
        }

      }
    }
  }
}
