package com.kabasoft.iws.service

trait Repository[F[_], A] {

  def create(item: A): F[Int]
  def delete(item: String): F[Int]
  def list(from: Int, until: Int): F[List[A]]
  def getBy(id: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[A]]
  def update(model: A): F[Int]
  def findSome(id: String): F[List[A]]
}
