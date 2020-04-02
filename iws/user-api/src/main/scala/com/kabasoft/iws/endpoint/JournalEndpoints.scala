package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Journal
import com.kabasoft.iws.service.JournalService
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class JournalEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: JournalService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: JournalService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "jou" =>
        for {
          transaction <- request.decodeJson[Journal]
          created <- service.create(transaction)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "jou" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "jou" =>
        for {
          journal <- request.decodeJson[Journal]
          updated <- service.update(journal)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "jou" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)
        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              journal = if (hasNext) retrieved.init else retrieved
              //response <- Ok(journal.asJson)
              response <- Ok("{ \"hits\": " + journal.asJson + " }") //, `Access-Control-Allow-Origin`("*"))

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }

    }

  private def get(service: JournalService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "jou" / (id) =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "joumd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

object JournalEndpoints {
  def apply[F[_]: Effect](service: JournalService[F]): HttpRoutes[F] =
    new JournalEndpoints[F].routes(service)
}
