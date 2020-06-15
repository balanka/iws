package com.kabasoft.iws.repository.doobie

import java.time.Instant
import java.time.temporal.ChronoField

import cats._
import cats.data._
import cats.effect.{IO, Sync}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import com.kabasoft.iws.domain._
import com.kabasoft.iws.domain.FinancialsTransaction.{FinancialsTransaction_Type, FinancialsTransaction_Type2}
import com.kabasoft.iws.domain.{Journal => DJournal, PeriodicAccountBalance => DPAC}
import com.kabasoft.iws.repository.doobie.SQL.{FinancialsTransaction, FinancialsTransactionDetails}
import com.kabasoft.iws.service.Service
import com.kabasoft.iws.repository.doobie.SQLPagination._
import com.kabasoft.iws.domain.common._
import com.kabasoft.iws.repository.doobie.Common.{getX, getXX, queryX}

import scala.language.higherKinds
object Common {

  def queryX[A](func: A => Query0[DPAC], query: A): ConnectionIO[Option[DPAC]] =
    for {
      response <- func(query).option
    } yield response

  def getX[A](func: A => Update0, query: A): ConnectionIO[Int] =
    for {
      response <- func(query).run
    } yield response

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
  def findSome(from: Int, until: Int, model: String*): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.findSome(model: _*))
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
  def findSome(from: Int, until: Int, model: String*): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.findSome(model: _*))
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
  //import com.kabasoft.iws.domain.common.parentAccountMonoid
  def create(item: Account): F[Int] = {
    println("item<<<<<<<<<<<", item);
    SQL.Account.create(item).run.transact(transactor)
  }

  def delete(id: String): F[Int] = SQL.Account.delete(id).run.transact(transactor)

  def list(from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.list)
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def getBy(id: String): F[Option[Account]] =
    SQL.Account
      .getBy(id)
      .map(Account.apply)
      .option
      .transact(transactor)

  def findSome(from: Int, until: Int, model: String*): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.findSome(model: _*))
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.getByModelId(modelid))
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def update(model: Account): F[List[Int]] = getXX(SQL.Account.update, List(model)).sequence.transact(transactor)

  def getBalances(accId: String, fromPeriod: Int, toPeriod: Int): F[Data] =
    (for {
      list <- SQL.Account.listX(fromPeriod, toPeriod).map(Account.apply).to[List]
      period = fromPeriod.toString.slice(0, 4).concat("00").toInt
      pacs <- SQL.PeriodicAccountBalance.find4Period(List(period, period)).to[List]
    } yield {
      //val acc = Account.consolidate("9900", list.filterNot((acc => acc.balance == 0 && acc.subAccounts.size == 0)))
      val acc = Account.consolidate(accId, list, pacs)
      val data = Account.consolidateData(acc)
      data
    }).transact(transactor)
}
case class ArticleService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Article] {
  def create(item: Article): F[Int] = {
    println("item<<<<<<<<<<<", item); SQL.Article.create(item).run.transact(transactor)
  }
  def delete(id: String): F[Int] = SQL.Article.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Article]] = SQL.Article.getBy(id).option.transact(transactor)
  def findSome(from: Int, until: Int, model: String*): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.findSome(model: _*)).to[List].transact(transactor)
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
  def findSome(from: Int, until: Int, model: String*): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.findSome(model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.getByModelId(modelid)).to[List].transact(transactor)

  def update(model: Vat): F[List[Int]] = getXX(SQL.Vat.update, List(model)).sequence.transact(transactor)
}
case class FinancialsTransactionService[F[_]: Sync](transactor: Transactor[F])
    extends Service[F, FinancialsTransaction] {

  import com.kabasoft.iws.domain.{FinancialsTransaction, FinancialsTransactionDetails => FTDetails}

  implicit val moveMonoid: Monoid[PeriodicAccountBalance] =
    new Monoid[PeriodicAccountBalance] {
      def empty =
        PeriodicAccountBalance(
          "",
          "0",
          BigDecimal(0),
          BigDecimal(0),
          BigDecimal(0),
          BigDecimal(0),
          "1000",
          "EUR"
        )

      def combine(m1: PeriodicAccountBalance, m2: PeriodicAccountBalance) =
        m2.idebiting(m1.idebit).icrediting(m1.icredit).debiting(m1.debit).crediting(m1.credit)
    }

  implicit val FinancialsTransactionDetailsMonoid: Monoid[FTDetails] =
    new Monoid[FTDetails] {
      def empty =
        FTDetails(0, 0, "", true, "", BigDecimal(0), Instant.now(), "", "EUR", "1000")

      def combine(m1: FTDetails, m2: FTDetails) = m2.copy(amount = m2.amount.+(m1.amount))
    }

  def create(item: FinancialsTransaction): F[Int] = SQL.FinancialsTransaction.create(item).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.FinancialsTransaction.delete(id).run.transact(transactor)

  def update(model: FinancialsTransaction): F[List[Int]] = {

    def insertPredicate(line: FinancialsTransactionDetails) = line.lid == -1L

    def deletePredicate(line: FinancialsTransactionDetails) = line.transid == -2L

    val splitted = model.lines.partition(insertPredicate(_))
    val splitted2 = splitted._2.partition(deletePredicate(_))
    println("model", model)
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

  def findSome(from: Int, until: Int, model: String*): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.findSome(model: _*))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.getByModelId(modelid))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def postAll(models: List[FinancialsTransaction]): F[List[Int]] =
    (for {
      all <- models.filter(_.posted == false).traverse(debitOrCreditPACAll(_))
    } yield all.flatten).transact(transactor)

  def post(model: FinancialsTransaction): F[List[Int]] = postAll(List(model))

  private[this] def debitOrCreditPACAll(model: FinancialsTransaction): ConnectionIO[List[Int]] =
    (
      for {
        pac <- getIds(model: FinancialsTransaction).traverse(SQL.PeriodicAccountBalance.getBy(_).option)
        oldRecords: List[DPAC] = getOldPac(pac, model)
        newRecords: List[DPAC] = getNewPac(pac, model)
        journalEntries: List[DJournal] = newJournalEntries(model, oldRecords ::: newRecords)
        pac_created <- newRecords.traverse(SQL.PeriodicAccountBalance.create(_).run)
        pac_updated <- oldRecords.traverse(SQL.PeriodicAccountBalance.update(_).run)
        trans_posted <- List(model.copy(posted = true).copy(postingdate = Instant.now()))
          .traverse(SQL.FinancialsTransaction.update2(_).run)
        journal <- journalEntries.traverse(SQL.Journal.create(_).run)
      } yield List(pac_created, pac_updated, journal, trans_posted).flatten
    )

  private[this] def getIds(model: FinancialsTransaction): List[String] = {
    val ids: List[String] = model.lines.map(line => DPAC.createId(model.getPeriod, line.account))
    val oids: List[String] = model.lines.map(line => DPAC.createId(model.getPeriod, line.oaccount))
    (ids ++ oids).toSet.toList
  }

  private[this] def getNewPac(pacList: List[Option[DPAC]], model: FinancialsTransaction): List[DPAC] = {
    val newRecords = getAndDebitCreditNewPacs(pacList, model.getPeriod, model.lines)
    println("newRecords", newRecords)
    val grouped: Iterable[DPAC] = newRecords
      .groupBy(_.id)
      .map({ case (_, v) => v.combineAll })
    val result = grouped.toList
    println("result", result)
    result
  }

  private[this] def getOldPac(pacList: List[Option[DPAC]], model: FinancialsTransaction): List[DPAC] = {
    val pacs: List[DPAC] = getAndDebitCreditOldPacs(pacList, model.getPeriod, model.lines)

    val result: List[DPAC] = pacs
      .groupBy(_.id)
      .map({ case (_, v) => v.combineAll })
      .toSet
      .toList
    println("result", result)
    result
  }

  def getOldPacs(pacList: List[Option[DPAC]], period: Int, acc: String): Option[DPAC] = {
    val pacId = DPAC.createId(period, acc)
    pacList.flatten.toSet.find(pac_ => pac_.id == pacId)
  }
  def debitIt(packList: List[DPAC], period: Int, line: FTDetails): Option[DPAC] =
    packList.find(pac_ => pac_.id == DPAC.createId(period, line.account)).map(_.debiting(line.amount))
  def creditIt(packList: List[DPAC], period: Int, line: FTDetails): Option[DPAC] =
    packList.find(pac_ => pac_.id == DPAC.createId(period, line.oaccount)).map(_.crediting(line.amount))

  private[this] def getAndDebitCreditOldPacs(
    pacList: List[Option[DPAC]],
    period: Int,
    lines: List[FTDetails]
  ): List[DPAC] = {

    val pacx1: List[DPAC] = lines.map(line => getOldPacs(pacList, period, line.account)).flatten.toSet.toList
    val poacx1: List[DPAC] = lines.map(line => getOldPacs(pacList, period, line.oaccount)).flatten.toSet.toList
    val groupedLines: List[FTDetails] = lines.groupBy(_.account).map({ case (_, v) => v.combineAll }).toList
    val groupedOLines: List[FTDetails] = lines.groupBy(_.oaccount).map({ case (_, v) => v.combineAll }).toList
    val pacx: List[DPAC] = groupedLines.map(line => debitIt(pacx1, period, line)).flatten
    val poacx: List[DPAC] = groupedOLines.map(line => creditIt(poacx1, period, line)).flatten

    Set(pacx, poacx).flatten.toList
  }
  private[this] def getAndDebitCreditNewPacs(
    pacList: List[Option[DPAC]],
    period: Int,
    lines: List[FTDetails]
  ): List[DPAC] = {

    val pacx1: List[(Option[DPAC], Boolean)] = lines
      .map(line => createIfNone(pacList, period, line, line.account))

    val pacx1x: List[DPAC] = pacx1.filter(_._2 == true).map(_._1).flatten.toSet.toList
    val poacx1: List[(Option[DPAC], Boolean)] = lines
      .map(line => createIfNone(pacList, period, line, line.oaccount))
    val poacx1x: List[DPAC] = poacx1.filter(_._2 == true).map(_._1).flatten.toSet.toList
    val groupedLines: List[FTDetails] = lines.groupBy(_.account).map({ case (_, v) => v.combineAll }).toList
    val groupedOLines: List[FTDetails] = lines.groupBy(_.oaccount).map({ case (_, v) => v.combineAll }).toList
    val pacx: List[DPAC] = groupedLines.map(line => debitIt(pacx1x, period, line)).flatten
    val poacx: List[DPAC] = groupedOLines.map(line => creditIt(poacx1x, period, line)).flatten
    Set(pacx, poacx).flatten.toList
  }

  private[this] def createPAC(accountId: String, period: Int, line: FTDetails): DPAC = {
    val zeroAmount = BigDecimal(0)
    DPAC.apply(
      accountId,
      period.toString,
      zeroAmount,
      zeroAmount,
      zeroAmount,
      zeroAmount,
      line.company,
      line.currency
    )
  }
  def createIfNone(
    pacList: List[Option[DPAC]],
    period: Int,
    line: FTDetails,
    accountId: String
  ): (Option[DPAC], Boolean) = {
    val pacId = DPAC.createId(period, accountId)
    val pacx: Option[DPAC] = pacList.flatten.toSet.find(pac_ => pac_.id == pacId)
    val pacO: (Option[DPAC], Boolean) =
      pacx match {
        case Some(pac) => {
          val x =
            if ((line.account == pac.account && line.side)
              || (line.oaccount == pac.account && !line.side)) Some(pac)
            else None
          (x, false)
        }
        case None =>
          val y =
            if (line.account == accountId) Some(createPAC(line.account, period, line))
            else if (line.oaccount == accountId) Some(createPAC(line.oaccount, period, line))
            else None
          (y, true)
      }
    pacO
  }

  private[this] def createJournalEntries(
    line: FTDetails,
    model: FinancialsTransaction,
    pacList: List[DPAC]
  ): List[DJournal] = {

    val pacId = DPAC.createId(model.getPeriod, line.account)
    val poacId = DPAC.createId(model.getPeriod, line.oaccount)
    val zeroAmount = BigDecimal(0)
    val pac: DPAC = pacList
      .find(pac_ => pac_.id == pacId)
      .getOrElse(DPAC.apply("", "", zeroAmount, zeroAmount, zeroAmount, zeroAmount, "", ""))
    val poac: DPAC = pacList
      .find(poac_ => poac_.id == poacId)
      .getOrElse(DPAC.apply("", "", zeroAmount, zeroAmount, zeroAmount, zeroAmount, "", ""))
    val jou1 = DJournal(
      -1,
      model.tid,
      model.oid,
      line.account,
      line.oaccount,
      model.transdate,
      model.postingdate,
      model.enterdate,
      model.getPeriod,
      line.amount,
      pac.idebit,
      pac.debit,
      pac.icredit,
      pac.credit,
      line.currency,
      line.side,
      line.text,
      model.month.toInt,
      model.year,
      model.company,
      model.typeJournal,
      model.file_content,
      model.modelid
    )
    val jou2 = DJournal(
      -1,
      model.tid,
      model.oid,
      line.oaccount,
      line.account,
      model.transdate,
      model.postingdate,
      model.enterdate,
      model.getPeriod,
      line.amount,
      poac.idebit,
      poac.debit,
      poac.icredit,
      poac.credit,
      line.currency,
      !line.side,
      line.text,
      model.month.toInt,
      model.year,
      model.company,
      model.typeJournal,
      model.file_content,
      model.modelid
    )
    List(jou1, jou2)
  }
  private[this] def newJournalEntries(model: FinancialsTransaction, pacList: List[DPAC]): List[DJournal] =
    model.lines.flatMap(line => createJournalEntries(line, model, pacList))

}
case class JournalService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Journal] {
  def create(item: Journal): F[Int] = SQL.Journal.create(item).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Journal.delete(id).run.transact(transactor)
  def list(from: Int, until: Int): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.list).to[List].transact(transactor)
  def getBy(id: String): F[Option[Journal]] = SQL.Journal.getBy(id).option.transact(transactor)
  def findSome(from: Int, until: Int, model: String*): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.findSome(model: _*)).to[List].transact(transactor)
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

DROP SEQUENCE IF EXISTS journal_id_seq ;
CREATE SEQUENCE journal_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807
   START 13641
   CACHE 1;
alter sequence journal_id_seq owner to postgres;
 */
