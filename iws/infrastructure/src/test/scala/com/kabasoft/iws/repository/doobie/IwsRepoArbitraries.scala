package com.kabasoft.iws.repository.doobie

import java.time.Instant

import cats.effect.IO
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck._
import tsec.authentication.AugmentedJWT
import tsec.common.SecureRandomId
import tsec.jws.mac._
import tsec.jwt.JWTClaims
import tsec.mac.jca._

trait IwsRepoArbitraries {
  val userNameLength = 16
  val userNameGen: Gen[String] = Gen.listOfN(userNameLength, Gen.alphaChar).map(_.mkString)

  implicit val instant = Arbitrary[Instant] {
    for {
      millis <- Gen.posNum[Long]
    } yield Instant.ofEpochMilli(millis)
  }

  implicit val role = Arbitrary[Role](Gen.oneOf(Role.values.toIndexedSeq))

  val modelid: Int = 111
  val company: String = "1000"
  implicit val user = Arbitrary[User] {
    for {
      userName <- userNameGen
      firstName <- arbitrary[String]
      lastName <- arbitrary[String]
      email <- arbitrary[String]
      //password <- arbitrary[String]
      phone <- arbitrary[String]
      id <- Gen.option(Gen.posNum[Long])
      hash <- arbitrary[String]
      role <- arbitrary[Role]
      menu <- arbitrary[String]
    } yield User(userName, firstName, lastName, hash, phone, email, role, menu, modelid, company, id)
  }

  case class AdminUser(value: User)
  case class CustomerUser(value: User)

  implicit val adminUser: Arbitrary[AdminUser] = Arbitrary {
    user.arbitrary.map(user => AdminUser(user.copy(role = Role.Admin)))
  }

  implicit val customerUser: Arbitrary[CustomerUser] = Arbitrary {
    user.arbitrary.map(user => CustomerUser(user.copy(role = Role.Customer)))
  }

  implicit val secureRandomId = Arbitrary[SecureRandomId] {
    arbitrary[String].map(SecureRandomId.apply)
  }

  implicit val jwtMac: Arbitrary[JWTMac[HMACSHA256]] = Arbitrary {
    for {
      key <- Gen.const(HMACSHA256.unsafeGenerateKey)
      claims <- Gen.finiteDuration.map(exp => JWTClaims.withDuration[IO](expiration = Some(exp)).unsafeRunSync())
    } yield JWTMacImpure
      .build[HMACSHA256](claims, key)
      .getOrElse(throw new Exception("Inconceivable"))
  }

  implicit def augmentedJWT[A, I](
    implicit arb1: Arbitrary[JWTMac[A]],
    arb2: Arbitrary[I]
  ): Arbitrary[AugmentedJWT[A, I]] =
    Arbitrary {
      for {
        id <- arbitrary[SecureRandomId]
        jwt <- arb1.arbitrary
        identity <- arb2.arbitrary
        expiry <- arbitrary[Instant]
        lastTouched <- Gen.option(arbitrary[Instant])
      } yield AugmentedJWT(id, jwt, identity, expiry, lastTouched)
    }
}
object IwsRepoArbitraries extends IwsRepoArbitraries
