package com.toracoya.petstore.pet

case class Pets(toList: List[Pet]) extends AnyVal {

  def count: Int = toList.size

  def init: Pets =
    if (isEmpty) {
      this
    } else {
      Pets(toList.init)
    }

  def isEmpty: Boolean = toList.isEmpty

  def slice(from: Int, until: Int): Pets = Pets(toList.slice(from, until))
}

object Pets {

  def apply(pets: Pet*): Pets = Pets(pets.toList)

  val empty: Pets = Pets(List.empty)
}

case class Masterfiles(toList: List[Masterfile]) extends AnyVal {

  def count: Int = toList.size

  def init: Masterfiles =
    if (isEmpty) {
      this
    } else {
      Masterfiles(toList.init)
    }

  def isEmpty: Boolean = toList.isEmpty

  def slice(from: Int, until: Int): Masterfiles = Masterfiles(toList.slice(from, until))
}
object Masterfiles {

  def apply(masterfiles: Masterfile*): Masterfiles = Masterfiles(masterfiles.toList)

  val empty: Masterfiles = Masterfiles(List.empty)
}
/*
case class Accounts(toList: List[Account]) extends AnyVal {

  def count: Int = toList.size

  def init: Accounts =
    if (isEmpty) {
      this
    } else {
      Accounts(toList.init)
    }

  def isEmpty: Boolean = toList.isEmpty

  def slice(from: Int, until: Int): Accounts = Accounts(toList.slice(from, until))
}
object Accounts {

  def apply(accounts: Account*): Accounts = Accounts(accounts.toList)

  val empty: Accounts = Accounts(List.empty)
}

 */
