/*
package com.toracoya.petstore.pets

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import com.toracoya.petstore.error.json.ErrorsJson
import com.toracoya.petstore.pagination.PaginationValidator
import com.toracoya.petstore.pet.{MasterfileId, Pet, PetService}
import com.toracoya.petstore.pets.PetEndpoints.MasterfileIdVar
import com.toracoya.petstore.pets.json.{PetJson, PetsJson}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import scala.util.Try

class PetEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  import com.toracoya.petstore.pagination.Pagination._
  //implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[F, A] = jsonOf[F, A]
  //implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[F, A] = jsonEncoderOf[F, A]

  def routes(service: PetService[F]): HttpRoutes[F] = get(service) <+> list(service)

  private def list(service: PetService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root =>
        for {
          pet <- request.decodeJson[Pet]
          created <- service.create(pet)
          resp <- Created(created.asJson)
        } yield resp

      case DELETE -> Root / "pets" / id =>
        service.delete(id) *>
          Ok()
      case request @ PATCH -> Root / "pet" =>
        for {
          pet <- request.decodeJson[Pet]
          updated <- service.update(pet)
          response <- Ok(updated.asJson)
        } yield response

      case GET -> Root / "petsmd" / IntVar(modelid) =>
        for {
          pets <- service.getByModelId(modelid)
          response <- Ok("{ \"hits\": " + pets.asJson + " }")
        } yield response

      case GET -> Root / "pets" :? PageMatcher(maybePage) :? PageSizeMatcher(maybePageSize) =>
        val page = maybePage.getOrElse(DefaultPage)
        val pageSize = maybePageSize.getOrElse(DefaultPageSize)

        PaginationValidator.validate(page, pageSize) match {
          case Valid(pagination) =>
            val (from, until) = pagination.range
            for {
              retrieved <- service.list(from, until + 1)
              hasNext = retrieved.size > until
              pet = if (hasNext) retrieved.init else retrieved
              response <- Ok(pet.asJson)
            } yield response
          case Invalid(errors) =>
            BadRequest(ErrorsJson.from(errors).asJson)
        }
    }

  private def get(service: PetService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "pets" / MasterfileIdVar(id) =>
        service.getBy(id).flatMap {article
          // case Some(found) => Ok(PetJson.from(found))
          case Some(found) => Ok(found.asJson)
          case None => NotFound("")
        }
    }
}

object PetEndpoints {

  private object MasterfileIdVar {
    def unapply(id: String): Option[MasterfileId] = Try(id).map(MasterfileId).toOption
  }

  def apply[F[_]: Effect](service: PetService[F]): HttpRoutes[F] = new PetEndpoints[F].routes(service)
}


 */
