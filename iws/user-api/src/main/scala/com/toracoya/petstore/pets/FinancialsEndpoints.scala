package com.toracoya.petstore.pets

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.toracoya.petstore.error.json.ErrorsJson
import com.toracoya.petstore.pagination.Pagination._
import com.toracoya.petstore.pagination.PaginationValidator
import com.toracoya.petstore.pet.{
  FinancialsTransaction,
  FinancialsTransactionDetailsService,
  FinancialsTransactionService
}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class FinancialsEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: FinancialsTransactionService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: FinancialsTransactionService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "ftr" =>
        for {
          transaction <- request.decodeJson[FinancialsTransaction]
          created <- service.create(transaction)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "ftr" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "ftr" =>
        for {
          transaction <- request.decodeJson[FinancialsTransaction]
          updated <- service.update(transaction)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "ftr" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)
        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              transaction = if (hasNext) retrieved.init else retrieved
              //response <- Ok(masterfile.asJson)
              response <- Ok("{ \"hits\": " + transaction.asJson + " }") //, `Access-Control-Allow-Origin`("*"))

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }

    }

  private def get(service: FinancialsTransactionService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "ftr" / (id) =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "ftrmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)
        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.getByModelId(modelid, from, until)
              hasNext = retrieved.size > until
              transaction = if (hasNext) retrieved.init else retrieved
              // tr <- transaction.map(x => x.copy(lines = service2.findSome(x.tid.toString)))
              response <- Ok("{ \"hits\": " + transaction.asJson + " }") //, `Access-Control-Allow-Origin`("*"))

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }
}
object FinancialsEndpoints {
  def apply[F[_]: Effect](
    service: FinancialsTransactionService[F],
    service2: FinancialsTransactionDetailsService[F]
  ): HttpRoutes[F] =
    new FinancialsEndpoints[F].routes(service)
}
