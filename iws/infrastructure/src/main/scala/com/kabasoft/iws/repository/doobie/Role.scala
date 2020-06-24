package com.kabasoft.iws.repository.doobie

import cats._
import cats.implicits._
import tsec.authorization.{AuthGroup, SimpleAuthEnum}

final case class Role(roleRepr: String)

object Role extends SimpleAuthEnum[Role, String] {
  val Admin: Role = Role("Admin")
  val Customer: Role = Role("Customer")
  val Accountant: Role = Role("Accountant")
  val Tester: Role = Role("Tester")

  override val values: AuthGroup[Role] = AuthGroup(Admin, Customer, Accountant, Tester)

  override def getRepr(t: Role): String = t.roleRepr

  implicit val eqRole: Eq[Role] = Eq.fromUniversalEquals[Role]
}
