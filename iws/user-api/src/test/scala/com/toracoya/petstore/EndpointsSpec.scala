package com.toracoya.petstore

import cats.effect.IO
import io.circe.Json
import io.circe.parser._
import org.http4s.{EntityDecoder, Response, Status}
import org.scalatest.WordSpec

trait EndpointsSpec extends WordSpec {

  def check[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
    implicit ev: EntityDecoder[IO, A]
  ): Boolean = {

    val actualResp = actual.unsafeRunSync
    val statusCheck = actualResp.status == expectedStatus

    val bodyCheck =
      expectedBody.fold[Boolean](actualResp.body.compile.toVector.unsafeRunSync.isEmpty)(
        expected => actualResp.as[A].unsafeRunSync == expected
      )
    statusCheck && bodyCheck
  }

  def toJson(jsonString: String): Json =
    parse(jsonString) match {
      case Right(json) => json
      case Left(_) => fail()
    }

}
