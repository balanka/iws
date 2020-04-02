package com.toracoya.petstore.repository.doobie

import com.toracoya.petstore.pet.{Account, MasterfileId}

class DoobieAccountRepositoryTest extends DoobieSpec {

  "#list" should {
    "return accounts" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieAccountRepository(transactor)
        val expects = allAccounts
        assert(repository.list(0, 20).unsafeRunSync() == expects)

        assert(repository.list(0, 2).unsafeRunSync() == expects.slice(0, 2))
        assert(repository.list(1, 2).unsafeRunSync() == expects.slice(1, 2))
      }
    }
  }

  "#getBy" when {
    "return an account" in new Fixture {
      withTransactor { transactor =>
        val repository = DoobieAccountRepository(transactor)

        assert(repository.getBy(accId1).unsafeRunSync() == Option(acc1))
        assert(repository.getBy(accId2).unsafeRunSync() == Option(acc2))
      //assert(repository.getBy(ghostaccId).unsafeRunSync().isEmpty)
      }
    }
  }

  trait Fixture {

    val accId1 = MasterfileId("121")
    val accId2 = MasterfileId("122")
    val accId3 = MasterfileId("123")
    val ghostaccId = MasterfileId("9999")

    val acc1 = Account(MasterfileId("121"), "Inventory", "ADMINISTRATION", 6, "0")
    val acc2 = Account(MasterfileId("122"), "Liability", "ADMINISTRATION", 6, "0")
    val acc3 = Account(MasterfileId("123"), "Cash", "ADMINISTRATION", 6, "0")
    val acc4 = Account(MasterfileId("124"), "Revenue", "ADMINISTRATION", 6, "0")
    val acc5 = Account(MasterfileId("125"), "Expenses", "ADMINISTRATION", 6, "0")
    val allAccounts = List(acc1, acc2, acc3, acc4, acc5)
  }
}
