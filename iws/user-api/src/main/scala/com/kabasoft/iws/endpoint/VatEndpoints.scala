package com.kabasoft.iws.endpoint
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.domain.Vat
import com.kabasoft.iws.error.json.ErrorsJson
import com.kabasoft.iws.pagination.Pagination._
import com.kabasoft.iws.pagination.PaginationValidator
import com.kabasoft.iws.repository.doobie.{User, VatService}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class VatEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  implicit val vatDecoder: EntityDecoder[F, Vat] = jsonOf[F, Vat]

  private def list(service: VatService[F]): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed _ =>
      for {
        vat <- req.request.decodeJson[Vat]
        created <- service.create(vat)
        resp <- Created(created.asJson)
      } yield resp
    case DELETE -> Root / id asAuthed user =>
      service.delete(id, user.company) *> Ok()

    case req @ PATCH -> Root asAuthed user =>
      for {
        vat <- req.request.decodeJson[Vat]
        updated <- service.update(vat, user.company)
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
            vat = if (hasNext) retrieved.init else retrieved
            response <- Ok(vat.asJson)
          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }

  private def get(service: VatService[F]): AuthEndpoint[F, Auth] = {
    case GET -> Root / id asAuthed user =>
      service.getBy(id, user.company).flatMap {
        case Some(found) => Ok(found.asJson)
        case None => NotFound("")
      }
    case GET -> Root / "vatmd" / IntVar(modelid) :? OffsetMatcher(maybePage) :? PageSizeMatcher(maybePageSize) asAuthed user =>
      val page = maybePage.getOrElse(DefaultPage)
      val pageSize = maybePageSize.getOrElse(DefaultPageSize)
      PaginationValidator.validate(page, pageSize) match {
        case Valid(pagination) =>
          val (from, until) = pagination.range
          for {
            retrieved <- service.getByModelId(modelid, from, until, user.company)
            hasNext = retrieved.size > until
            vat = if (hasNext) retrieved.init else retrieved
            response <- Ok(vat.asJson)
          } yield response
        case Invalid(errors) =>
          BadRequest(ErrorsJson.from(errors).asJson)
      }
  }
  def endpoints(
    service: VatService[F],
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

object VatEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
    service: VatService[F],
    auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]]
  ): HttpRoutes[F] = new VatEndpoints[F, Auth].endpoints(service, auth)
}
