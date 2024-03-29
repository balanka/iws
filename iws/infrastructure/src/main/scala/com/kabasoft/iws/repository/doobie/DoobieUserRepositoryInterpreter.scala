package com.kabasoft.iws.repository.doobie

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import com.kabasoft.iws.repository.UserRepository
import com.kabasoft.iws.repository.doobie.SQLPagination._
import doobie._
import doobie.implicits._
//import io.circe.parser.decode
//import io.circe.syntax._
import tsec.authentication.IdentityStore

private object UserSQL {

  def insert(user: User): Update0 = sql"""
    INSERT INTO USERS (USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, HASH, PHONE, COMPANY, ROLE)
    VALUES (${user.userName}, ${user.firstName}, ${user.lastName}, ${user.email}, ${user.hash}, ${user.phone}
    , ${user.company}, ${user.role})""".update

  def update(user: User, id: Long): Update0 =
    sql"""UPDATE USERS
    SET FIRST_NAME = ${user.firstName}, LAST_NAME = ${user.lastName},
        EMAIL = ${user.email}, HASH = ${user.hash}, PHONE = ${user.phone}, COMPANY= ${user.company}, ROLE = ${user.role}
    WHERE ID = ${id}
  """.update

  def select(userId: Long): Query0[User] = sql"""
      SELECT u.USER_NAME, u.FIRST_NAME, u.LAST_NAME, u.HASH, u.PHONE, u.EMAIL, u.ROLE
      ,  string_agg(moduleid, ', ') AS menu, u.MODELID,  u.COMPANY, u.ID
    FROM USERS U,  USERMENU m
    WHERE u.ID = m.userid and u.ID= ${userId} group by u.id
  """.query

  def byUserName(userName: String): Query0[User] = sql"""
    SELECT u.USER_NAME, u.FIRST_NAME, u.LAST_NAME, u.HASH, u.PHONE, u.EMAIL, u.ROLE
    ,  string_agg(moduleid, ', ') AS menu, u.MODELID,  u.COMPANY, u.ID
    FROM USERS U,  USERMENU m
    WHERE u.ID = m.userid and u.USER_NAME= ${userName} group by u.id
  """.query[User]

  def delete(userId: Long): Update0 = sql"""
    DELETE FROM USERS WHERE ID = $userId
  """.update

  val selectAll: Query0[User] = sql"""
    SELECT u.USER_NAME, u.FIRST_NAME, u.LAST_NAME, u.HASH, u.PHONE, u.EMAIL, u.ROLE
    ,  string_agg(moduleid, ', ') AS menu, u.MODELID,  u.COMPANY, u.ID
    FROM USERS U,  USERMENU m
    WHERE u.ID = m.userid  group by u.id
  """.query
}

class DoobieUserRepositoryInterpreter[F[_]: Sync](val xa: Transactor[F])
    extends UserRepository[F]
    with IdentityStore[F, Long, User] { self =>
  import UserSQL._

  def create(user: User): F[User] =
    insert(user).withUniqueGeneratedKeys[Long]("id").map(id => user.copy(id = id.some)).transact(xa)

  def update(user: User): OptionT[F, User] =
    OptionT.fromOption[F](user.id).semiflatMap { id =>
      UserSQL.update(user, id).run.transact(xa).as(user)
    }

  def get(userId: Long): OptionT[F, User] = OptionT(select(userId).option.transact(xa))

  def findByUserName(userName: String): OptionT[F, User] =
    OptionT(byUserName(userName).option.transact(xa))

  def delete(userId: Long): OptionT[F, User] =
    get(userId).semiflatMap(user => UserSQL.delete(userId).run.transact(xa).as(user))

  def deleteByUserName(userName: String): OptionT[F, User] =
    findByUserName(userName).mapFilter(_.id).flatMap(delete)

  def list(pageSize: Int, offset: Int): F[List[User]] =
    paginate(pageSize, offset)(selectAll).to[List].transact(xa)
}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Sync](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter(xa)
}
