package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.service.PeriodicAccountBalanceService
import com.kabasoft.iws.domain.PeriodicAccountBalance
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class PeriodicAccountBalanceEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: PeriodicAccountBalanceService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: PeriodicAccountBalanceService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "pac" =>
        for {
          masterfile <- request.decodeJson[PeriodicAccountBalance]
          created <- service.create(masterfile)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "pac" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "pac" =>
        for {
          pac <- request.decodeJson[PeriodicAccountBalance]
          updated <- service.update(pac)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "cust" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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
//http://localhost:8080/pets/pac/331034/202001/202004
  private def get(service: PeriodicAccountBalanceService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "pac" / accountid / from / to => {
        for {
          retrieved <- service.findSome(accountid, from, to)
          response <- Ok("{ \"hits\": " + retrieved.asJson + " }")
        } yield response
      }
      case GET -> Root / "pacmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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
object PeriodicAccountBalanceEndpoints {
  def apply[F[_]: Effect](service: PeriodicAccountBalanceService[F]): HttpRoutes[F] =
    new PeriodicAccountBalanceEndpoints[F].routes(service)
}
