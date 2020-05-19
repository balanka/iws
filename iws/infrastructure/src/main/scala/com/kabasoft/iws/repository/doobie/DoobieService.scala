package com.kabasoft.iws.repository.doobie

import cats._
import cats.data._
import cats.effect.{IO, Sync}
import cats.implicits._
import cats.effect.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import doobie.free.connection

import com.kabasoft.iws.domain._
import com.kabasoft.iws.domain.FinancialsTransaction.{FinancialsTransaction_Type, FinancialsTransaction_Type2}
import com.kabasoft.iws.repository.doobie.SQL.{FinancialsTransaction, FinancialsTransactionDetails}
import com.kabasoft.iws.service.Service
import com.kabasoft.iws.repository.doobie.SQLPagination._
import com.kabasoft.iws.repository.doobie.Common.{getX, getXX}

import scala.language.higherKinds
object Common {

  def getX[A](func: A => Update0, query: A): ConnectionIO[Int] =
    for {
      response <- func(query).run
    } yield response //.transact(transactor)
  def getXX[A](func: List[A] => List[Update0], query: List[A]): List[ConnectionIO[Int]] =
    for {
      response <- func(query).map(_.run)
    } yield response
}

case class BankService[F[_]: Sync](transactor: Transactor[F] /*, repository1: Repository[Bank, Bank]*/ )
    extends Service[F, Bank] {

  def create(item: Bank): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Bank.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Bank.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Bank]] = SQL.Bank.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Bank]] =
    paginate(1000000 - 1, 1)(SQL.Bank.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.getByModelId(modelid)).to[List].transact(transactor)
  def update(model: Bank): F[List[Int]] = getXX(SQL.Bank.update, List(model)).sequence.transact(transactor)

}
case class BankStatementService[F[_]: Sync](
  transactor: Transactor[F]
  //repository: Repository[BankStatement, BankStatement]
) extends Service[F, BankStatement] {
  def create(item: BankStatement): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.BankStatement.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.BankStatement.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[BankStatement]] = SQL.BankStatement.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[BankStatement]] =
    paginate(1000000 - 1, 1)(SQL.BankStatement.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.getByModelId(modelid)).to[List].transact(transactor)
  def update(models: BankStatement): F[List[Int]] =
    getXX(SQL.BankStatement.update, List(models)).sequence.transact(transactor)
}
case class MasterfileService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Masterfile] {
  def create(item: Masterfile): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Masterfile.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Masterfile.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.list)
      .to[List]
      .transact(transactor)
  def getBy(id: String): F[Option[Masterfile]] = SQL.Masterfile.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Masterfile]] =
    paginate(1000000 - 1, 1)(SQL.Masterfile.findSome(model: _*))
      .to[List]
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.getByModelId(modelid))
      .to[List]
      .transact(transactor)
  def update(model: Masterfile): F[List[Int]] =
    getXX(SQL.Masterfile.update, List(model)).sequence.transact(transactor)
}
case class CostCenterService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, CostCenter] {
  def create(item: CostCenter): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.CostCenter.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.CostCenter.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.list)
      .to[List]
      .transact(transactor)
  def getBy(id: String): F[Option[CostCenter]] = SQL.CostCenter.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[CostCenter]] =
    paginate(1000000 - 1, 1)(SQL.CostCenter.findSome(model: _*))
      .to[List]
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.getByModelId(modelid))
      .to[List]
      .transact(transactor)
  def update(model: CostCenter): F[List[Int]] =
    getXX(SQL.CostCenter.update, List(model)).sequence.transact(transactor)

}
case class AccountService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Account] {
  def create(item: Account): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Account.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Account.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.list)
      .to[List]
      .transact(transactor)
  def getBy(id: String): F[Option[Account]] = SQL.Account.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Account]] =
    paginate(1000000 - 1, 1)(SQL.Account.findSome(model: _*))
      .to[List]
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.getByModelId(modelid))
      .to[List]
      .transact(transactor)
  def update(model: Account): F[List[Int]] = getXX(SQL.Account.update, List(model)).sequence.transact(transactor)

}

case class ArticleService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Article] {
  def create(item: Article): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Article.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Article.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Article]] = SQL.Article.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Article]] =
    paginate(1000000 - 1, 1)(SQL.Article.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Article): F[List[Int]] = getXX(SQL.Article.update, List(model)).sequence.transact(transactor)
}

case class CustomerService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Customer] {
  def create(item: Customer): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Customer.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Customer.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Customer]] = SQL.Customer.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Customer]] =
    paginate(1000000 - 1, 1)(SQL.Customer.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Customer): F[List[Int]] = getXX(SQL.Customer.update, List(model)).sequence.transact(transactor)
}
case class SupplierService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Supplier] {
  def create(item: Supplier): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Supplier.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Supplier.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Supplier]] = SQL.Supplier.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Supplier]] =
    paginate(1000000 - 1, 1)(SQL.Supplier.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Supplier): F[List[Int]] = getXX(SQL.Supplier.update, List(model)).sequence.transact(transactor)

}
case class PeriodicAccountBalanceService[F[_]: Sync](
  transactor: Transactor[F]
) extends Service[F, PeriodicAccountBalance] {
  def create(item: PeriodicAccountBalance): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.PeriodicAccountBalance.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.PeriodicAccountBalance.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[PeriodicAccountBalance]] =
    SQL.PeriodicAccountBalance.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[PeriodicAccountBalance]] =
    paginate(1000000 - 1, 1)(SQL.PeriodicAccountBalance.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: PeriodicAccountBalance): F[List[Int]] =
    getXX(SQL.PeriodicAccountBalance.update, List(model)).sequence.transact(transactor)
}
case class RoutesService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Routes] {
  def create(item: Routes): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Routes.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Routes.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Routes]] = SQL.Routes.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Routes]] =
    paginate(1000000 - 1, 1)(SQL.Routes.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Routes): F[List[Int]] = getXX(SQL.Routes.update, List(model)).sequence.transact(transactor)
}
case class VatService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Vat] {
  def create(item: Vat): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Vat.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Vat.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Vat]] = SQL.Vat.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Vat]] =
    paginate(1000000 - 1, 1)(SQL.Vat.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Vat): F[List[Int]] = getXX(SQL.Vat.update, List(model)).sequence.transact(transactor)
}
case class FinancialsTransactionService[F[_]: Sync: Applicative](transactor: Transactor[F])
    extends Service[F, FinancialsTransaction] {

  import com.kabasoft.iws.domain.{FinancialsTransaction, FinancialsTransactionDetails}

  def create(item: FinancialsTransaction): F[Int] = SQL.FinancialsTransaction.create(item).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.FinancialsTransaction.delete(id).run.transact(transactor)

  def update(model: FinancialsTransaction): F[List[Int]] = {

    def insertPredicate(line: FinancialsTransactionDetails) = line.lid == -1L
    def deletePredicate(line: FinancialsTransactionDetails) = line.transid == -2L
    val splitted = model.lines.partition(insertPredicate(_))
    val splitted2 = splitted._2.partition(deletePredicate(_))
    println("splitted1", splitted)
    println("splitted2", splitted2)
    val newLines = splitted._1
    val deletedLineIds = splitted2._1.map(line => line.id)
    val oldLines = splitted2._2
    println("INSERT->" + newLines.size, newLines);
    println("DELETE->" + deletedLineIds.size, deletedLineIds);
    println("OLD->" + oldLines.size, oldLines);
    val result: List[ConnectionIO[Int]] =
      getXX(SQL.FinancialsTransactionDetails.update, oldLines) ++
        getXX(SQL.FinancialsTransactionDetails.delete, deletedLineIds) ++
        getXX(SQL.FinancialsTransactionDetails.create, newLines) ++
        List(getX(SQL.FinancialsTransaction.update, model))

    result.sequence.transact(transactor)

  }

  def list(from: Int, until: Int): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.list)
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def getBy(id: String): F[Option[FinancialsTransaction]] =
    SQL.FinancialsTransaction
      .getBy(id)
      .to[List]
      .map(FinancialsTransaction.apply)
      .map(_.headOption)
      .transact(transactor)

  def findSome(model: String*): F[List[FinancialsTransaction]] =
    paginate(100000 - 1, 1)(SQL.FinancialsTransaction.findSome(model: _*))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.getByModelId(modelid))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)
}

case class JournalService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Journal] {
  def create(item: Journal): F[Int] = SQL.Journal.create(item).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Journal.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Journal]] = SQL.Journal.getBy(id).option.transact(transactor)
  def findSome(model: String*): F[List[Journal]] =
    paginate(1000000 - 1, 1)(SQL.Journal.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.getByModelId(modelid)).to[List].transact(transactor)
  def update(model: Journal): F[List[Int]] = getXX(SQL.Journal.update, List(model)).sequence.transact(transactor)

}
/*
DROP SEQUENCE IF EXISTS details_compta_id_seq ;
CREATE SEQUENCE details_compta_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 5141
    CACHE 1;
alter sequence details_compta_id_seq owner to postgres;

 */
