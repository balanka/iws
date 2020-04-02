package com.toracoya.petstore.pets

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.toracoya.petstore.error.json.ErrorsJson
import com.toracoya.petstore.pagination.Pagination._
import com.toracoya.petstore.pagination.PaginationValidator
import com.toracoya.petstore.pet.{Supplier, SupplierService}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class SupplierEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: SupplierService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: SupplierService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "sup" =>
        for {
          masterfile <- request.decodeJson[Supplier]
          created <- service.create(masterfile)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "sup" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "sup" =>
        for {
          masterfile <- request.decodeJson[Supplier]
          updated <- service.update(masterfile)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "sup" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

  private def get(service: SupplierService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "sup" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "supmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

object SupplierEndpoints {
  def apply[F[_]: Effect](service: SupplierService[F]): HttpRoutes[F] = new SupplierEndpoints[F].routes(service)
}
