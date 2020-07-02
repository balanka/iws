package com.kabasoft.iws.repository.doobie

import cats.Applicative
import cats.data.EitherT
import cats.implicits._
import com.kabasoft.iws.repository.UserRepository

class UserValidationInterpreter[F[_]: Applicative](userRepo: UserRepository[F]) extends UserValidation[F] {
  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit] =
    userRepo
      .findByUserName(user.userName)
      .map(UserAlreadyExistsError)
      .toLeft(())

  def exists(userId: Option[Long]): EitherT[F, UserNotFoundError.type, Unit] =
    userId match {
      case Some(id) =>
        userRepo
          .get(id)
          .toRight(UserNotFoundError)
          .void
      case None =>
        EitherT.left[Unit](UserNotFoundError.pure[F])
    }
}

object UserValidationInterpreter {
  def apply[F[_]: Applicative](repo: UserRepository[F]): UserValidation[F] =
    new UserValidationInterpreter[F](repo)
}
