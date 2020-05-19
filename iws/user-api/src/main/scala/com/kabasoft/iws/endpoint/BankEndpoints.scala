package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Bank
import com.kabasoft.iws.repository.doobie.BankService
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class BankEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: BankService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: BankService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case request @ POST -> Root / "bank" =>
        for {
          bank <- request.decodeJson[Bank]
          created <- service.create(bank)
          resp <- Created(created.asJson)
        } yield resp
      case DELETE -> Root / "bank" / id =>
        service.delete(id) *> Ok()

      case request @ PATCH -> Root / "bank" =>
        for {
          bank <- request.decodeJson[Bank]
          updated <- service.update(bank)
          response <- Ok(updated.asJson)
        } yield response

      case GET -> Root / "bank" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

  private def get(service: BankService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "bank" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "bankmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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
object BankEndpoints {
  def apply[F[_]: Effect](service: BankService[F]): HttpRoutes[F] = new BankEndpoints[F].routes(service)
}
