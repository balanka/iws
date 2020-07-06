package com.kabasoft.iws.service
trait Service[F[_], A] {
  def create(item: A): F[Int]
  def delete(item: String, company: String): F[Int]
  def list(from: Int, until: Int, company: String): F[List[A]]
  def getBy(id: String, company: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[A]]
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[A]]
  def update(model: A, company: String): F[List[Int]]
}
