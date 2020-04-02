package com.toracoya.petstore.pets.json

import com.toracoya.petstore.pet.{Masterfile, Pet}

case class PetJson(id: String, name: String)
case class MasterfileJson(id: String, name: String, description: String)

object PetJson {

  def from(pet: Pet): PetJson = PetJson(pet.id.value, pet.name.toString)
}
object MasterfileJson {

  def from(mf: Masterfile): MasterfileJson = MasterfileJson(mf.id.value, mf.name.toString, mf.description)
}
