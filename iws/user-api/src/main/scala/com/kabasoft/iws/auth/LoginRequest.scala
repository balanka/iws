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
  company: String,
  role: Role
) {
  def asUser[A](hashedPassword: PasswordHash[A]): User = User(
    userName,
    firstName,
    lastName,
    email,
    hashedPassword.toString,
    phone,
    company,
    role = role
  )
}
