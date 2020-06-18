package com.kabasoft.iws.config

import com.kabasoft.iws.repository.doobie.DatabaseConfig
import io.circe.Decoder
import io.circe.generic.semiauto._

case class AppConfig(incomeStmtAccountId: String, balanceSheetAccountId: String)
case class IwsConfig(host: String, port: Int, pageSize: Int, pageCount: Int, db: DatabaseConfig, app: AppConfig)

object IwsConfig {
  implicit val iwsConfigDecoder: Decoder[IwsConfig] = deriveDecoder
}
object AppConfig {
  implicit val appConfigDecoder: Decoder[AppConfig] = deriveDecoder
}
