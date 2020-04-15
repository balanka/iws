package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination.{PageSizeMatcher, _}
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Vat
import com.kabasoft.iws.service.VatService
import com.kabasoft.iws.pagination.Pagination._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._

import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.{Decoder, Encoder}
import java.time.Instant
import scala.util.Try

class VatEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emapTry { str =>
    Try(Instant.parse(str))

  }

  def routes(service: VatService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: VatService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case request @ POST -> Root / "vat" =>
        for {
          vat <- request.decodeJson[Vat]
          created <- service.create(vat)
          resp <- Created(created.asJson)
        } yield resp
      case DELETE -> Root / "vat" / id =>
        service.delete(id) *> Ok()

      case request @ PATCH -> Root / "vat" =>
        for {
          vat <- request.decodeJson[Vat]
          updated <- service.update(vat)
          response <- Ok(updated.asJson)
        } yield response

      case GET -> Root / "vat" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)

        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              vat = if (hasNext) retrieved.init else retrieved
              response <- Ok("{ \"hits\": " + vat.asJson + " }")
            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }

  private def get(service: VatService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "vat" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "vatmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)
        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.getByModelId(modelid, from, until)
              hasNext = retrieved.size > until
              vat = if (hasNext) retrieved.init else retrieved
              response <- Ok("{ \"hits\": " + vat.asJson + " }")

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }
}
object VatEndpoints {
  def apply[F[_]: Effect](service: VatService[F]): HttpRoutes[F] = new VatEndpoints[F].routes(service)
}
