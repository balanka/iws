package com.kabasoft.iws.endpoint

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.domain.Supplier
import com.kabasoft.iws.repository.doobie.{SupplierService, User}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class SupplierEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  private def list(service: SupplierService[F]): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed _ =>
      for {
        masterfile <- req.request.decodeJson[Supplier]
        created <- service.create(masterfile)
        resp <- Created(created.asJson)
      } yield resp

    case DELETE -> Root / id asAuthed user =>
      service.delete(id, user.company) *> Ok()
    case req @ PATCH -> Root asAuthed user =>
      for {
        masterfile <- req.request.decodeJson[Supplier]
        updated <- service.update(masterfile, user.company)
        response <- Ok(updated.asJson)
      } yield response

    case GET -> Root :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      getResponse(page, pageSize, user.company, service.list, service)
  }

  def bankAccounts(s: Supplier, company: String, service: SupplierService[F]) =
    for {
      bankAccouts_ <- service.bankAccounts(s.id, company)
      supplier = s.copy(bankaccounts = bankAccouts_)
    } yield supplier

  def getResponse(
    page: Int,
    pageSize: Int,
    company: String,
    call: (Int, Int, String) => F[List[Supplier]],
    service: SupplierService[F]
  ) =
    PaginationValidator.validate(page, pageSize) match {
      case Valid(pagination) =>
        val (from, until) = pagination.range
        for {
          retrieved <- call(from, until, company)
          hasNext = retrieved.size > until
          list = if (hasNext) retrieved.init else retrieved
          trx <- list.traverse(s => bankAccounts(s, company, service))
          response <- Ok(trx.asJson )
          //response <- Ok("{ \"hits\": " + trx.asJson + " }")
        } yield response
      case Invalid(errors) =>
        BadRequest(ErrorsJson.from(errors).asJson)
    }

  def getResponse(
    modelid: Int,
    page: Int,
    pageSize: Int,
    company: String,
    call: (Int, Int, Int, String) => F[List[Supplier]],
    service: SupplierService[F]
  ) =
    PaginationValidator.validate(page, pageSize) match {
      case Valid(pagination) =>
        val (from, until) = pagination.range
        for {
          retrieved <- call(modelid, from, until, company)
          hasNext = retrieved.size > until
          list = if (hasNext) retrieved.init else retrieved
          trx <- list.traverse(s => bankAccounts(s, company, service))
          response <- Ok(trx.asJson )
          //response <- Ok("{ \"hits\": " + trx.asJson + " }")
        } yield response
      case Invalid(errors) =>
        BadRequest(ErrorsJson.from(errors).asJson)
    }

  private def get(service: SupplierService[F]): AuthEndpoint[F, Auth] = {
    case GET -> Root / "bankacc" / id asAuthed user =>
      for {
        bankaccounts <- service.getBankAccounts(id, user.company)
        response <- Ok(bankaccounts.asJson)
      } yield response
    case GET -> Root / id asAuthed user =>
      service.getBy(id, user.company).flatMap {
        case Some(found) => Ok(found.asJson)
        case None => NotFound("")
      }
    case GET -> Root / "supmd" / IntVar(modelid) :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      getResponse(modelid, page, pageSize, user.company, service.getByModelId, service)
  }
  def endpoints(
    service: SupplierService[F],
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

object SupplierEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: SupplierService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new SupplierEndpoints[F, Auth].endpoints(service, auth)
}
