package com.toracoya.petstore.repository.doobie

import cats.effect.{ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import doobie.util.transactor.Transactor
import io.circe.config.parser
import org.scalactic.source
import org.scalatest.{Assertion, WordSpec}

import scala.concurrent.ExecutionContext

trait DoobieSpec extends WordSpec {

  implicit private val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def withTransactor(f: Transactor[IO] => Assertion)(implicit pos: source.Position): Assertion = {
    val resource = for {
      config <- Resource.liftF(parser.decodePathF[IO, DatabaseConfig](ConfigFactory.load("database"), "db"))
      transactor <- config.transactor[IO]
    } yield transactor

    resource
      .use { transactor =>
        IO { f(transactor) }
      }
      .unsafeRunSync()
  }

}
