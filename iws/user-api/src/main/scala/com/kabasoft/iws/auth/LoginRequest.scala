package com.kabasoft.iws.auth

import com.kabasoft.iws.repository.doobie.{Role, User}
import tsec.passwordhashers.PasswordHash

final case class LoginRequest(
  userName: String,
  password: String
)

final case class SignupRequest(
  userName: String,
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  phone: String,
  role: Role,
  menu: String = "",
  modelid: Int = 111,
  company: String
) {
  def asUser[A](hashedPassword: PasswordHash[A]): User =
    User(userName, firstName, lastName, hashedPassword.toString, phone, email, role, menu, modelid, company)
}
