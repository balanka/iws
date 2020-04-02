package com.toracoya.petstore.pets
import java.util.Date
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.toracoya.petstore.error.json.ErrorsJson
import com.toracoya.petstore.pagination.PaginationValidator
import com.toracoya.petstore.pagination.Pagination.PageSizeMatcher
import com.toracoya.petstore.pagination.Pagination._
import com.toracoya.petstore.pet.{CostCenter, CostCenterService}
import io.circe.{Decoder, Encoder}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import cats.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class CostCenterEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: CostCenterService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: CostCenterService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / "cc" =>
        for {
          masterfile <- request.decodeJson[CostCenter]
          created <- service.create(masterfile)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "cc" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "cc" =>
        for {
          masterfile <- request.decodeJson[CostCenter]
          updated <- service.update(masterfile)
          response <- Ok(updated.asJson) //.putHeaders(Header("X-Auth-Token", "value"))
        } yield response

      case GET -> Root / "cc" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

  private def get(service: CostCenterService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "cc" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
      case GET -> Root / "ccmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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

object CostCenterEndpoints {
  def apply[F[_]: Effect](service: CostCenterService[F]): HttpRoutes[F] = new CostCenterEndpoints[F].routes(service)
}
