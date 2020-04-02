package com.toracoya.petstore.repository.doobie

import com.toracoya.petstore.pet.{Article, MasterfileId}

class DoobieArticleRepositoryTest extends DoobieSpec {

  "#list" should {
    "return articles" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieArticleRepository(transactor)
        val expects = allArticles
        val l = repository.list(0, 20).unsafeRunSync()
        assert(l.size == expects.size)

        assert(repository.list(0, 2).unsafeRunSync().size == expects.slice(0, 2).size)
        assert(repository.list(1, 2).unsafeRunSync().size == expects.slice(1, 2).size)
      }
    }
  }

  "#getBy" when {
    "return an article" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieArticleRepository(transactor)

        assert(repository.getBy(artId6).unsafeRunSync().get.id == Option(art6).get.id)
        assert(repository.getBy(artId2).unsafeRunSync().get.id == Option(art2).get.id)
      //assert(repository.getBy(ghostaccId).unsafeRunSync().isEmpty)
      }
    }
  }

  trait Fixture {

    val artId1 = MasterfileId("1")
    val artId2 = MasterfileId("2")
    val artId3 = MasterfileId("3")
    val artId6 = MasterfileId("6")
    val ghostaccId = MasterfileId("9999")

    val art1 = Article(MasterfileId("1"), "IWS", "Integrated Warehouse Management System", 8, "0", 100000.00, false)
    val art2 = Article(MasterfileId("2"), "Masterfiles", "Masterfiles", 8, "1", 10000.00, false)
    val art3 = Article(MasterfileId("3"), "Purchasinhg", "Purchasinhg", 8, "1", 20000.00, false)
    val art4 = Article(MasterfileId("4"), "Inventory", "Inventory", 8, "1", 40000.00, false)
    val art5 = Article(MasterfileId("5"), "Financials", "Financials", 8, "1", 50000.00, false)
    val art6 = Article(MasterfileId("6"), "Ai", "Ai", 8, "1", 120000.00, false)
    val allArticles = List(art1, art2, art3, art4, art5, art6)
  }
}
