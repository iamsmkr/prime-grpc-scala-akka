package com.iamsmkr.primeroxy

import com.iamsmkr.primegenerator.grpc._
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent._
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Route

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}
import akka.http.scaladsl.common.{CsvEntityStreamingSupport, EntityStreamingSupport}
import akka.http.scaladsl.marshalling.{Marshaller, Marshalling}
import akka.util.ByteString
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.iamsmkr.primecommon._
import org.slf4j.Logger

class PrimeRoutes(log: Logger, client: PrimeGeneratorServiceClient)(implicit mat: Materializer, ec: ExecutionContext) {

  import PrimeRoutes._

  val routes: Route =
    cors(corsSettings) {
      withRequestTimeout(TIMEOUT_DURATION) {
        
        path("prime" / LongNumber) { number =>
          val startByteString = ByteString("$start$")

          implicit val streamingSupport: CsvEntityStreamingSupport =
            EntityStreamingSupport.csv(maxLineLength = 16 * 1024)
              .withSupported(ContentTypeRange(ContentTypes.`text/plain(UTF-8)`))
              .withContentType(ContentTypes.`text/plain(UTF-8)`)
              .withFramingRenderer(
                Flow[ByteString].sliding(2, 1)
                  .map { bsSeq =>
                    if (startByteString.equals(bsSeq.head)) bsSeq(1)
                    else ByteString(",") ++ bsSeq(1)
                  }
              )

          complete {
            client.getPrimeNumbers(GetPrimeNumbersRequest(number))
              .map(_.primeNumber)
              .map(i => ByteString(i.toString)).prepend(Source.single(startByteString))
              .map(bs => HttpEntity(ContentTypes.`text/plain(UTF-8)`, bs))
          }

        } ~ path("prime" / LongNumber / "csv-stream") { number =>
          get {
            log.info(s"prime numbers up until number $number are requested as csv stream")

            implicit val csvFormat = Marshaller.strict[GetPrimeNumbersReply, ByteString] { res =>
              Marshalling.WithFixedContentType(ContentTypes.`text/csv(UTF-8)`, () => {
                ByteString(List(res.primeNumber).mkString(","))
              })
            }

            implicit val streamingSupport: CsvEntityStreamingSupport = EntityStreamingSupport.csv()

            val res = validateNumberArg(number)

            if (res.isDefined) complete(BadRequest, res.get.msg)
            else complete(client.getPrimeNumbers(GetPrimeNumbersRequest(number)))
          }

        } ~ path("prime" / LongNumber / "seq") { number =>
          get {
            log.info(s"prime numbers up until number $number are requested")

            val res = validateNumberArg(number)

            if (res.isDefined) complete(BadRequest, res.get.msg)
            else {
              val f = client.getPrimeNumbers(GetPrimeNumbersRequest(number))
                .map(_.primeNumber)
                .take(MAX_ALLOWED_SIZE)
                .runWith(Sink.seq)
                .map(_.mkString(","))

              onComplete(f) {
                case Success(reply) => complete(reply)
                case Failure(t) =>
                  log.error("Request failed", t)
                  complete(StatusCodes.InternalServerError, t.getMessage)
              }
            }

          }

        } ~ path("prime" / LongNumber / "sse") { number =>
          get {
            log.info(s"prime numbers up until number $number are requested as sse")

            val res = validateNumberArg(number)

            if (res.isDefined) complete(BadRequest, res.get.msg)
            else {
              complete {
                client.getPrimeNumbers(GetPrimeNumbersRequest(number))
                  .map(_.primeNumber)
                  .map(n => ServerSentEvent(n.toString))
                  .keepAlive(1.second, () => ServerSentEvent.heartbeat)
              }
            }

          }

        } ~ path("prime" / Remaining) { _ =>
          complete(BadRequest, "Please provide a number greater than 1")

        } ~ path(Remaining) { _ =>
          complete(NotFound, "Prime numbers can be streamed @/prime/:number")
        }
      }
    }
}

object PrimeRoutes {
  def apply(log: Logger, client: PrimeGeneratorServiceClient)(implicit mat: Materializer, ec: ExecutionContext): PrimeRoutes =
    new PrimeRoutes(log, client)(mat, ec)

  private val MAX_ALLOWED_SIZE = 10000

  private val TIMEOUT_DURATION = 60.minutes

  private lazy val corsSettings = CorsSettings.defaultSettings.withAllowedMethods(List(HttpMethods.GET))
}
