package com.toracoya.petstore.config

import com.toracoya.petstore.repository.doobie.DatabaseConfig
import io.circe.Decoder
import io.circe.generic.semiauto._

case class PetStoreConfig(host: String, port: Int, pageSize: Int, pageCount: Int, db: DatabaseConfig)

object PetStoreConfig {

  implicit val petStoreDecoder: Decoder[PetStoreConfig] = deriveDecoder
}
