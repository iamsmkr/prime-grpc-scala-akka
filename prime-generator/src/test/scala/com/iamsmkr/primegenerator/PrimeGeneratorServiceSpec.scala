package com.iamsmkr.primegenerator

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import akka.testkit._
import com.iamsmkr.primegenerator.grpc.GetPrimeNumbersRequest
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class PrimeGeneratorServiceSpec extends TestKit(ActorSystem("PrimeGeneratorServerTest")) with AnyWordSpecLike
  with should.Matchers with ScalaCheckPropertyChecks with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  implicit val ec: ExecutionContext = system.dispatcher

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
            PrimeGeneratorServiceImpl(system.log)
              .getPrimeNumbers(GetPrimeNumbersRequest(number))
              .map(_.primeNumber)
              .runWith(Sink.seq)
              .map(_.mkString(",")),
            5.seconds
          ) should be(result)
        }
      }
    }
  }
}
