package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Account
import com.kabasoft.iws.repository.doobie.{AccountService, User}
import com.kabasoft.iws.pagination.Pagination._
import io.circe.generic.auto._
import org.http4s.{EntityDecoder, HttpRoutes}
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import com.kabasoft.iws.auth.Auth
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo
class AccountEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  implicit val accountDecoder: EntityDecoder[F, Account] = jsonOf[F, Account]

  private def list(service: AccountService[F]): AuthEndpoint[F, Auth] = {

    case req @ POST -> Root asAuthed _ =>
      for {
        account <- req.request.decodeJson[Account]
        created <- service.create(account)
        resp <- Created(created.asJson)
      } yield resp
    case DELETE -> Root / id asAuthed user =>
      service.delete(id, user.company) *>
        Ok()

    case req @ PATCH -> Root asAuthed user =>
      for {
        account <- req.request.decodeJson[Account]
        updated <- service.update(account, user.company)
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
            account = if (hasNext) retrieved.init else retrieved
            response <- Ok("{ \"hits\": " + account.asJson + " }")
          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
    case GET -> Root / "balance" / account / from / to asAuthed user =>
      val acc = account
      val fromPeriod = from.toInt
      val toPeriod = to.toInt
      for {
        account <- service.getBalances(acc, fromPeriod, toPeriod, user.company)
        response <- Ok("{ \"hits\": " + account.asJson + " }")
      } yield response

    case GET -> Root / "close" / from / to asAuthed user =>
      val fromPeriod = from.toInt
      val toPeriod = to.toInt
      for {
        account <- service.closePeriod(fromPeriod, toPeriod, user.company)
        response <- Ok("{\"hits\":" + account.asJson + "}")
      } yield response
  }

  private def get(service: AccountService[F]): AuthEndpoint[F, Auth] = {
    case GET -> Root / id asAuthed user =>
      service.getBy(id, user.company).flatMap {
        case Some(found) => Ok(found.asJson)
        case None => NotFound("")
      }
    case GET -> Root / "accmd" / IntVar(modelid) :? OffsetMatcher(maybePage)
          :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.getByModelId(modelid, from, until, user.company)
            hasNext = retrieved.size > until
            transaction = if (hasNext) retrieved.init else retrieved
            response <- Ok("{ \"hits\": " + transaction.asJson + " }")

          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }

  def endpoints(
    service: AccountService[F],
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

object AccountEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: AccountService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new AccountEndpoints[F, Auth].endpoints(service, auth)
}
