package com.kabasoft.iws.repository.doobie

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import doobie.hikari.HikariTransactor
import io.circe.Decoder
import io.circe.generic.semiauto._
import org.flywaydb.core.Flyway
import cats.syntax.functor._
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

  def initializeDb[F[_]](cfg: DatabaseConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
        val fw: Flyway = {
          Flyway
            .configure()
            .dataSource(cfg.url, cfg.user, cfg.password)
            .load()
        }
        fw.migrate()
      }
      .as(())

  implicit val dbConfigDecoder: Decoder[DatabaseConfig] = deriveDecoder
}

object DatabaseConnectionsConfig {
  implicit val dbconnDec: Decoder[DatabaseConnectionsConfig] = deriveDecoder
}
