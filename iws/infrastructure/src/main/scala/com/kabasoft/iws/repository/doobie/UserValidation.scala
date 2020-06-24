package com.kabasoft.iws.repository.doobie

import cats.data.EitherT

trait UserValidation[F[_]] {
  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit]

  def exists(userId: Option[Long]): EitherT[F, UserNotFoundError.type, Unit]
}
