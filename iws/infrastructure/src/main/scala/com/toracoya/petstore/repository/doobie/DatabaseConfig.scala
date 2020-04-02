package com.toracoya.petstore.repository.doobie

import cats.effect.{Async, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.circe.Decoder
import io.circe.generic.semiauto._
import java.util.concurrent.Executors
import cats.effect.Blocker
import doobie._
import doobie.implicits._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class ConnectionConfig(poolSize: Int)

case class DatabaseConfig(url: String, driver: String, user: String, password: String, connection: ConnectionConfig) {

  def transactor[F[_]: Async: ContextShift]: Resource[F, HikariTransactor[F]] =
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[F](connection.poolSize)
      transactor <- HikariTransactor.newHikariTransactor[F](
        driver,
        url,
        user,
        password,
        connectEC,
        Blocker.liftExecutorService(Executors.newCachedThreadPool())
      )
    } yield transactor

  def transactor2[Id[_]: Async: ContextShift]: Resource[Id, HikariTransactor[Id]] =
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[Id](connection.poolSize)
      transactor <- HikariTransactor.newHikariTransactor[Id](
        driver,
        url,
        user,
        password,
        connectEC,
        Blocker.liftExecutorService(Executors.newCachedThreadPool())
      )
    } yield transactor
}

object DatabaseConfig {

  implicit val connectionDecoder: Decoder[ConnectionConfig] = deriveDecoder

  implicit val dataBaseDecoder: Decoder[DatabaseConfig] = deriveDecoder
}
