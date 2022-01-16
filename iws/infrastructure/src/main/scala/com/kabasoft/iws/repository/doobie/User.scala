package com.kabasoft.iws.repository.doobie

import cats.Applicative
import tsec.authorization.AuthorizationInfo

case class User(
  userName: String,
  firstName: String,
  lastName: String,
  hash: String,
  phone: String,
  email: String,
  role: Role,
  menu: String = "",
  modelid: Int = 111,
  company: String = "1000",
  id: Option[Long] = None
)

object User {
  implicit def authRole[F[_]](implicit F: Applicative[F]): AuthorizationInfo[F, Role, User] = u => F.pure(u.role)
}
