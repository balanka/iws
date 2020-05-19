package com.kabasoft.iws.service
import scala.language.higherKinds

trait Service[F[_], A] {
  def create(item: A): F[Int]
  def delete(item: String): F[Int]
  def list(from: Int, until: Int): F[List[A]]
  def getBy(id: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[A]]
  def findSome(model: String*): F[List[A]]
  def update(model: A): F[List[Int]]
}
