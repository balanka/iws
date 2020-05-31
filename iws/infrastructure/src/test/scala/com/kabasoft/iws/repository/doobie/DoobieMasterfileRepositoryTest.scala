package com.kabasoft.iws.repository.doobie

/*
import com.toracoya.petstore.pet.{Masterfile, MasterfileId}

class DoobieMasterfileRepositoryTest extends DoobieSpec {

  "#list" should {
    "return masterfiles" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieMasterfileRepository(transactor)
        val expects = all
        assert(repository.list(0, 20).unsafeRunSync() == expects)

        assert(repository.list(0, 2).unsafeRunSync() == expects.slice(0, 2))
        assert(repository.list(1, 2).unsafeRunSync() == expects.slice(1, 2))
      }
    }
  }

  "#getBy" when {
    "return a masterfile" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieMasterfileRepository(transactor)

        assert(repository.getBy(Id1).unsafeRunSync() == Option(m1))
        assert(repository.getBy(Id2).unsafeRunSync() == Option(m2))
      //assert(repository.getBy(ghostaccId).unsafeRunSync().isEmpty)
      }
    }
  }

  trait Fixture {

    val Id1 = MasterfileId("1")
    val Id2 = MasterfileId("2")
    val Id3 = MasterfileId("3")
    val ghostaccId = MasterfileId("9999")

    val m1 = Masterfile(MasterfileId("1"), "IWS", "IWS", 0, "0")
    val m2 = Masterfile(MasterfileId("2"), "Masterfiles", "Masterfiles", 0, "0")
    val m3 = Masterfile(MasterfileId("3"), "Accounting", "Accounting", 0, "0")
    val m4 = Masterfile(MasterfileId("4"), "SCM", "Supply Chain Management", 0, "0")
    val m5 = Masterfile(MasterfileId("5"), "Sales", "Customer Relationship Management", 0, "0")
    val all = List(m1, m2, m3, m4, m5)
  }
}

 */
