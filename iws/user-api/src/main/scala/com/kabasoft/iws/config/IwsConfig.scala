package com.kabasoft.iws.config

import com.kabasoft.iws.repository.doobie.DatabaseConfig
import io.circe.Decoder
import io.circe.generic.semiauto._

final case class ServerConfig(host: String, port: Int)
final case class AppConfig(incomeStmtAccountId: String, balanceSheetAccountId: String)
final case class IwsConfig(server: ServerConfig, pageSize: Int, pageCount: Int, db: DatabaseConfig, app: AppConfig)
object IwsConfig {
  implicit val iwsConfigDecoder: Decoder[IwsConfig] = deriveDecoder
}

object ServerConfig {
  implicit val cvrConfigDecoder: Decoder[ServerConfig] = deriveDecoder
}
object AppConfig {
  implicit val appConfigDecoder: Decoder[AppConfig] = deriveDecoder
}
