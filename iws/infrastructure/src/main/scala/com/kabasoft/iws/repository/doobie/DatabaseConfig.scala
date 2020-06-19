package com.kabasoft.iws.repository.doobie

import cats.syntax.functor._
import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import doobie.hikari.HikariTransactor
import io.circe.Decoder
import io.circe.generic.semiauto._
import cats.effect.Blocker
import scala.concurrent.ExecutionContext
case class DatabaseConnectionsConfig(poolSize: Int)

case class DatabaseConfig(
  url: String,
  driver: String,
  user: String,
  password: String,
  connections: DatabaseConnectionsConfig
)

object DatabaseConfig {
  def dbTransactor[F[_]: Async: ContextShift](
    dbc: DatabaseConfig,
    connEc: ExecutionContext,
    blocker: Blocker
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor
      .newHikariTransactor[F](dbc.driver, dbc.url, dbc.user, dbc.password, connEc, blocker)
  implicit val dbConfigDecoder: Decoder[DatabaseConfig] = deriveDecoder
}

object DatabaseConnectionsConfig {
  implicit val dbconnDec: Decoder[DatabaseConnectionsConfig] = deriveDecoder
}
