package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.domain.BankStatement
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination.{PageSizeMatcher, _}
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.repository.doobie.{BankStatementService, User}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class BankStatementEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  private def list(service: BankStatementService[F]): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed _ =>
      for {
        masterfile <- req.request.decodeJson[BankStatement]
        created <- service.create(masterfile)
        resp <- Created(created.asJson)
      } yield resp
    case req @ PATCH -> Root / "post" asAuthed user =>
      for {
        ids <- req.request.decodeJson[List[Long]]
        updated <- service.postAll(ids, user.company)
        response <- Ok(updated.asJson)
      } yield response
    case DELETE -> Root / id asAuthed user =>
      service.delete(id, user.company) *> Ok()
    case req @ PATCH -> Root asAuthed user =>
      for {
        masterfile <- req.request.decodeJson[BankStatement]
        updated <- service.update(masterfile, user.company)
        response <- Ok(updated.asJson)
      } yield response

    case GET -> Root :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)

      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.list(from, until + 1, user.company)
            hasNext = retrieved.size > until
            masterfile = if (hasNext) retrieved.init else retrieved
            response <- Ok(masterfile.asJson)
            //response <- Ok("{ \"hits\": " + masterfile.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }

  private def get(service: BankStatementService[F]): AuthEndpoint[F, Auth] = {
    case GET -> Root / id asAuthed user =>
      service.getBy(id, user.company).flatMap {
        case Some(found) => Ok(found.asJson)
        case None => NotFound("")
      }
    case GET -> Root / "bsmd" / IntVar(modelid) :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.getByModelId(modelid, from, until, user.company)
            hasNext = retrieved.size > until
            transaction = if (hasNext) retrieved.init else retrieved
            response <- Ok(transaction.asJson)
            //response <- Ok("{ \"hits\": " + transaction.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }
  def endpoints(
    service: BankStatementService[F],
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

object BankStatementEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: BankStatementService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new BankStatementEndpoints[F, Auth].endpoints(service, auth)
}
