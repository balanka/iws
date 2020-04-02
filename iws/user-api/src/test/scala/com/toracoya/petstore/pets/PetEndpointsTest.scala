/*
package com.toracoya.petstore.pets

import cats.effect.IO
import com.toracoya.petstore.APIValidation.InvalidPage
import com.toracoya.petstore.EndpointsSpec
import com.toracoya.petstore.pet._
import io.circe.Json
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.{HttpRoutes, Method, Request, Status, Uri}
import org.mockito.ArgumentMatchersSugar.anyInt
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar

class PetEndpointsTest extends EndpointsSpec with MockitoSugar {

  "GET /pets" should {
    "return pets and `hasNext` that false" in new Fixture {
      private val response = endpoints.orNotFound.run(
        Request(method = Method.GET, uri = Uri.uri("/pets"))
      )
      when(service.list(anyInt, anyInt)).thenReturn(IO(pets))

      assert(check[Json](response, Status.Ok, Option(petsJson(Pets(pets), hasNext = false))))
    }
  }

  "GET /pets/?pageSize=1" should {
    "return pets and `hasNext` that true" in new Fixture {
      private val response = endpoints.orNotFound.run(
        Request(method = Method.GET, uri = Uri.uri("/pets/?pageSize=1"))
      )
      when(service.list(anyInt, anyInt)).thenReturn(IO(pets))

      assert(check[Json](response, Status.Ok, Option(petsJson(Pets(pets), hasNext = true))))
    }
  }

  "GET /pets?page=-1" should {
    "return errors" in new Fixture {
      private val response = endpoints.orNotFound.run(
        Request(method = Method.GET, uri = Uri.uri("/pets/?page=-1"))
      )
      val errors = List(InvalidPage.message)

      assert(check[Json](response, Status.BadRequest, Option(errorsJson(errors))))
    }
  }

  "GET /pets/1" should {
    "return the pet id = 1" in new Fixture {
      private val response = endpoints.orNotFound.run(
        Request(method = Method.GET, uri = Uri.uri("/pets/1"))
      )
      when(service.getBy(petId1)).thenReturn(IO(Option(pet1)))
      System.out.println(" response ++++++++++>" + response)
      assert(check[Json](response, Status.Ok, Option(petJson(pet1))))
    }
  }

  "GET /pets/9999" should {
    "return Not Found" in new Fixture {
      private val response = endpoints.orNotFound.run(
        Request(method = Method.GET, uri = Uri.uri("/pets/9999"))
      )
      when(service.getBy(ghostPetId)).thenReturn(IO(None))
      System.out.println(" response ++++++++++>" + response)
      assert(check[Json](response, Status.NotFound, None))
    }
  }

  trait Fixture {
    val service: PetService[IO] = mock[PetService[IO]]
    val endpoints: HttpRoutes[IO] = PetEndpoints.apply[IO](service)

    val petId1 = MasterfileId("1")
    val petId2 = MasterfileId("2")
    val ghostPetId = MasterfileId("9999")

    val pet1 = Pet(petId1, MasterfileName("Bailey"))
    val pet2 = Pet(petId2, MasterfileName("Bella"))
    val pets = List(pet1, pet2)
  }

  private def petsJson(pets: Pets, hasNext: Boolean): Json = {
    val inner = pets.toList
      .map { pet =>
        s"""
         {
           "id": ${pet.id.value},
           "name": "${pet.name.toString}"
         }
       """
      }
      .mkString(",")

    toJson(s"""
       {
         "pets": [$inner],
         "hasNext": $hasNext
       }""")
  }

  private def petJson(pet: Pet): Json =
    toJson(s"""
         {
           "id": ${pet.id.value},
           "name": "${pet.name.toString}"
         }
       """)

  private def errorsJson(errors: List[String]): Json = {
    val inner = errors
      .map { error =>
        s"""
         {
           "message": "$error"
         }
       """
      }
      .mkString(",")

    toJson(s"""
       {
         "errors": [$inner]
       }
     """)
  }

}


 */
