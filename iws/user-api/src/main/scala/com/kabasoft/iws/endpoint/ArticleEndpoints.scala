package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Article
import com.kabasoft.iws.repository.doobie.ArticleService
import com.kabasoft.iws.pagination.Pagination._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class ArticleEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def routes(service: ArticleService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: ArticleService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case request @ POST -> Root / "art" =>
        for {
          article <- request.decodeJson[Article]
          created <- service.create(article)
          resp <- Created(created.asJson)
        } yield resp

      case request @ PATCH -> Root / "art" =>
        for {
          article <- request.decodeJson[Article]
          updated <- service.update(article)
          response <- Ok(updated.asJson)
        } yield response

      //case req @ PATCH -> Root / LongVar(id) =>
      case DELETE -> Root / "art" / id =>
        service.delete(id) *>
          Ok()

      case GET -> Root / "art" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)

        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              article = if (hasNext) retrieved.init else retrieved
              response <- Ok(article.asJson)

            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }

  private def get(service: ArticleService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "art" / id =>
        service.getBy(id).flatMap {
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }

      case GET -> Root / "artmd" / IntVar(modelid) :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
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
object ArticleEndpoints {

  def apply[F[_]: Effect](service: ArticleService[F]): HttpRoutes[F] = new ArticleEndpoints[F].routes(service)
}
