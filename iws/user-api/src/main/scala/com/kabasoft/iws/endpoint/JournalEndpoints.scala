package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Journal
import com.kabasoft.iws.repository.doobie.{JournalService, User}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class JournalEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  implicit val journalDecoder: EntityDecoder[F, Journal] = jsonOf[F, Journal]

  private def list(service: JournalService[F]): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed _ =>
      for {
        transaction <- req.request.decodeJson[Journal]
        created <- service.create(transaction)
        resp <- Created(created.asJson)
      } yield resp

    case DELETE -> Root / id asAuthed _ =>
      service.delete(id) *>
        Ok()
    case req @ PATCH -> Root asAuthed _ =>
      for {
        journal <- req.request.decodeJson[Journal]
        updated <- service.update(journal)
        response <- Ok(updated.asJson)
      } yield response

    case GET -> Root :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed _ =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.list(from, until + 1)
            hasNext = retrieved.size > until
            journal = if (hasNext) retrieved.init else retrieved
            response <- Ok("{ \"hits\": " + journal.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }

  }

  private def get(service: JournalService[F]): AuthEndpoint[F, Auth] = {
    case GET -> Root / accountid / fromPriod / toPeriod asAuthed _ => {
      val page = DefaultPage
      val pageSize = DefaultPageSize
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.findSome(from, until, accountid, fromPriod, toPeriod)
            response <- Ok("{ \"hits\": " + retrieved.asJson + " }")
          } yield response

        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
    }
    case GET -> Root / (id) asAuthed _ =>
      service.getBy(id).flatMap {
        case Some(found) => Ok(found.asJson)
        case None => NotFound("")
      }
    case GET -> Root / "joumd" / IntVar(modelid) :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed _ =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.getByModelId(modelid, from, until)
            hasNext = retrieved.size > until
            transaction = if (hasNext) retrieved.init else retrieved
            response <- Ok("{ \"hits\": " + transaction.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }
  def endpoints(
    service: JournalService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = {
    val authEndpoints: AuthService[F, Auth] = {
      Auth.allRolesHandler(list(service).orElse(get(service))) {
        Auth.adminOnly(list(service).orElse(get(service)))
      }
    }

    auth.liftService(authEndpoints)
  }
}

object JournalEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: JournalService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new JournalEndpoints[F, Auth].endpoints(service, auth)
}
