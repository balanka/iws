package com.toracoya.petstore.pet

import cats.Monad

trait Repository[F[_], A] {

  def create(item: A): F[Int]
  def delete(item: String): F[Int]
  def list(from: Int, until: Int): F[List[A]]
  def getBy(id: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[A]]
  def update(model: A): F[Int]
  def findSome(id: String): F[List[A]]
}
/*
trait PetRepository[F[_]] extends Repository[F, Pet]
trait MasterfileRepository[F[_]] extends Repository[F, Masterfile]
trait AccountRepository[F[_]] extends Repository[F, Account]
trait ArticleRepository[F[_]] extends Repository[F, Article]
 */
