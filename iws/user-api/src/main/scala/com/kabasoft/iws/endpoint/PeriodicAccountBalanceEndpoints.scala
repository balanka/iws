package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.repository.doobie.{PeriodicAccountBalanceService, User}
import com.kabasoft.iws.domain.PeriodicAccountBalance
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class PeriodicAccountBalanceEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  implicit val pacDecoder: EntityDecoder[F, PeriodicAccountBalance] = jsonOf[F, PeriodicAccountBalance]

  private def list(service: PeriodicAccountBalanceService[F]): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed _ =>
      for {
        masterfile <- req.request.decodeJson[PeriodicAccountBalance]
        created <- service.create(masterfile)
        resp <- Created(created.asJson)
      } yield resp

    case DELETE -> Root / id asAuthed _ =>
      service.delete(id) *>
        Ok()
    case req @ PATCH -> Root asAuthed _ =>
      for {
        pac <- req.request.decodeJson[PeriodicAccountBalance]
        updated <- service.update(pac)
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
            masterfile = if (hasNext) retrieved.init else retrieved
            response <- Ok("{ \"hits\": " + masterfile.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }
//http://localhost:8080/pets/pac/331034/202001/202004
  private def get(service: PeriodicAccountBalanceService[F]): AuthEndpoint[F, Auth] = {
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
    case GET -> Root / "pacmd" / IntVar(modelid) :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed _ =>
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
    service: PeriodicAccountBalanceService[F],
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

object PeriodicAccountBalanceEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: PeriodicAccountBalanceService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new PeriodicAccountBalanceEndpoints[F, Auth].endpoints(service, auth)
}
