package com.kabasoft.iws.repository.doobie

import com.toracoya.petstore.pet.{MasterfileId, Pet}

class DoobiePetRepositoryTest extends DoobieSpec {

  "#list" should {
    "return pets" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobiePetRepository(transactor)
        val expects = allPets

        assert(repository.list(0, 20).unsafeRunSync() == expects)

        assert(repository.list(0, 2).unsafeRunSync() == expects.slice(0, 2))
        assert(repository.list(1, 2).unsafeRunSync() == expects.slice(1, 2))
      }
    }
  }

  "#getBy" when {
    "return a pet" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobiePetRepository(transactor)

        assert(repository.getBy(petId1).unsafeRunSync() == Option(pet1))
        assert(repository.getBy(petId2).unsafeRunSync() == Option(pet2))
      //assert(repository.getBy(ghostPetId).unsafeRunSync().isEmpty)
      }
    }
  }

  trait Fixture {

    val petId1 = MasterfileId("1")
    val petId2 = MasterfileId("2")
    val petId3 = MasterfileId("3")
    val ghostPetId = MasterfileId("9999")

    val pet1 = Pet(MasterfileId("1"), "Bailey")
    val pet2 = Pet(MasterfileId("2"), "Bella")
    val pet3 = Pet(MasterfileId("3"), "Max")

    val allPets = List(pet1, pet2, pet3)
  }
}
