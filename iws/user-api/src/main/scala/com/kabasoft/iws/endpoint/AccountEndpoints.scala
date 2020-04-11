package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Account
import com.kabasoft.iws.service.AccountService
import com.kabasoft.iws.pagination.Pagination._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.{Decoder, Encoder}
import java.time.Instant
import scala.util.Try

class AccountEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emapTry { str =>
    Try(Instant.parse(str))

  }

  def routes(service: AccountService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: AccountService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case request @ POST -> Root / "acc" =>
        for {
          account <- request.decodeJson[Account]
          created <- service.create(account)
          resp <- Created(created.asJson)
        } yield resp
      case DELETE -> Root / "acc" / id =>
        service.delete(id) *>
          Ok()

      case request @ PATCH -> Root / "acc" =>
        for {
          account <- request.decodeJson[Account]
          updated <- service.update(account)
          response <- Ok(updated.asJson)
        } yield response

      case GET -> Root / "acc" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)

        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              account = if (hasNext) retrieved.init else retrieved
              //response <- Ok(account.asJson)
              response <- Ok("{ \"hits\": " + account.asJson + " }")
            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }

  private def get(service: AccountService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "acc" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "accmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)
        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.getByModelId(modelid, from, until)
              hasNext = retrieved.size > until
              transaction = if (hasNext) retrieved.init else retrieved
              response <- Ok("{ \"hits\": " + transaction.asJson + " }") //, `Access-Control-Allow-Origin`("*"))

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }
}
object AccountEndpoints {
  def apply[F[_]: Effect](service: AccountService[F]): HttpRoutes[F] = new AccountEndpoints[F].routes(service)
}
