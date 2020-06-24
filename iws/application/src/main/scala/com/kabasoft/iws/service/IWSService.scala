package com.kabasoft.iws.service
trait Service[F[_], A] {
  def create(item: A): F[Int]
  def delete(item: String): F[Int]
  def list(from: Int, until: Int): F[List[A]]
  def getBy(id: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[A]]
  def findSome(from: Int, until: Int, model: String*): F[List[A]]
  def update(model: A): F[List[Int]]
}
