package com.toracoya.petstore.pets.json

import com.toracoya.petstore.pet.{Account, Article, Masterfile, Pet}

case class PetsJson(pets: List[Pet], hasNext: Boolean)
case class MasterfilesJson(masterfiles: List[Masterfile], hasNext: Boolean)
case class AccountsJson(accounts: List[Account], hasNext: Boolean)
case class ArticlesJson(articles: List[Article], hasNext: Boolean)

object PetsJson {

  def from(pets: List[Pet], hasNext: Boolean): PetsJson = PetsJson(pets, hasNext)
  //def from(pets: Pets, hasNext: Boolean): PetsJson = PetsJson(pets.toList, hasNext)
}

object MasterfilesJson {

  def from(masterfiles: List[Masterfile], hasNext: Boolean): MasterfilesJson = MasterfilesJson(masterfiles, hasNext)
}
object AccountsJson {

  def from(accounts: List[Account], hasNext: Boolean): AccountsJson = AccountsJson(accounts, hasNext)
}

object ArticlesJson {

  def from(articles: List[Article], hasNext: Boolean): ArticlesJson = ArticlesJson(articles, hasNext)
}
