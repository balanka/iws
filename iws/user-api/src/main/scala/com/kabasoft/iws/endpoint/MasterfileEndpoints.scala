package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Masterfile
import com.kabasoft.iws.service.MasterfileService
import com.kabasoft.iws.pagination.Pagination._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{Header, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Access-Control-Allow-Origin`

import scala.util.Try

class MasterfileEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: MasterfileService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: MasterfileService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "mf" =>
        for {
          masterfile <- request.decodeJson[Masterfile]
          created <- service.create(masterfile)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "mf" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "mf" =>
        for {
          masterfile <- request.decodeJson[Masterfile]
          updated <- service.update(masterfile)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "mf" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)

        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              masterfile = if (hasNext) retrieved.init else retrieved
              //response <- Ok(masterfile.asJson)
              response <- Ok("{ \"hits\": " + masterfile.asJson + " }") //, `Access-Control-Allow-Origin`("*"))

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }

  private def get(service: MasterfileService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "mf" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "mfmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

object MasterfileEndpoints {

  def apply[F[_]: Effect](service: MasterfileService[F]): HttpRoutes[F] = new MasterfileEndpoints[F].routes(service)
}