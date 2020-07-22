package com.kabasoft.iws.repository.doobie

import java.time.Instant
import cats._
import cats.effect.Sync
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import com.kabasoft.iws.domain._
import com.kabasoft.iws.domain.{Journal => DJournal, PeriodicAccountBalance => DPAC}
import com.kabasoft.iws.service.Service
import com.kabasoft.iws.repository.doobie.SQLPagination._
import com.kabasoft.iws.domain.PeriodicAccountBalance.pacMonoid
import com.kabasoft.iws.repository.doobie.Common.{getX, getXX}

object Common {

  def queryX[A](func: A => Query0[DPAC], query: A): ConnectionIO[Option[DPAC]] =
    for {
      response <- func(query).option
    } yield response

  def getX[A](func: (A, String) => Update0, query: A, company: String): ConnectionIO[Int] =
    for {
      response <- func(query, company).run
    } yield response

  def getX[A](func: A => Update0, query: A): ConnectionIO[Int] =
    for {
      response <- func(query).run
    } yield response

  def getXX[A](func: (List[A], String) => List[Update0], query: List[A], company: String): List[ConnectionIO[Int]] =
    for {
      response <- func(query, company).map(_.run)
    } yield response

  def getXX[A](func: List[A] => List[Update0], query: List[A]): List[ConnectionIO[Int]] =
    for {
      response <- func(query).map(_.run)
    } yield response
}

case class BankService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Bank] {

  def create(item: Bank): F[Int] = SQL.Bank.create(item).run.transact(transactor)

  def delete(id: String, company: String): F[Int] = SQL.Bank.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Bank]] = SQL.Bank.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Bank]] =
    paginate(until - from, from)(SQL.Bank.getByModelId(modelid, company)).to[List].transact(transactor)
  def update(model: Bank, company: String): F[List[Int]] =
    getXX(SQL.Bank.update, List(model), company).sequence.transact(transactor)

}
case class BankStatementService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, BankStatement] {
  def insert(items: List[BankStatement]) = {
    println("Data loaded!!!!" + SQL.BankStatement.create(items(0)).sql)
    getXX(SQL.BankStatement.create, items).sequence.transact(transactor)
  }
  //def insert(items: List[BankStatement])=(for {
  //  bs_created <- items.traverse(create(_).run)
  //} yield bs_created).transact(transactor)
  //SQL.BankStatement.insertSQL.updateMany(items).transact(transactor)
  def create(item: BankStatement): F[Int] = SQL.BankStatement.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.BankStatement.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[BankStatement]] =
    SQL.BankStatement.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[BankStatement]] =
    paginate(until - from, from)(SQL.BankStatement.getByModelId(modelid, company)).to[List].transact(transactor)
  def update(models: BankStatement, company: String): F[List[Int]] =
    getXX(SQL.BankStatement.update, List(models), company).sequence.transact(transactor)
}
case class MasterfileService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Masterfile] {
  def create(item: Masterfile): F[Int] = SQL.Masterfile.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Masterfile.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.list(company))
      .to[List]
      .transact(transactor)
  def getBy(id: String, company: String): F[Option[Masterfile]] =
    SQL.Masterfile.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.findSome(company, model: _*))
      .to[List]
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Masterfile]] =
    paginate(until - from, from)(SQL.Masterfile.getByModelId(modelid, company))
      .to[List]
      .transact(transactor)
  def update(model: Masterfile, company: String): F[List[Int]] =
    getXX(SQL.Masterfile.update, List(model), company).sequence.transact(transactor)
}
case class CostCenterService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, CostCenter] {
  def create(item: CostCenter): F[Int] = SQL.CostCenter.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.CostCenter.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.list(company))
      .to[List]
      .transact(transactor)
  def getBy(id: String, company: String): F[Option[CostCenter]] =
    SQL.CostCenter.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.findSome(company, model: _*))
      .to[List]
      .transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[CostCenter]] =
    paginate(until - from, from)(SQL.CostCenter.getByModelId(modelid, company))
      .to[List]
      .transact(transactor)
  def update(model: CostCenter, company: String): F[List[Int]] =
    getXX(SQL.CostCenter.update, List(model), company).sequence.transact(transactor)

}
case class AccountService[F[_]: Sync](transactor: Transactor[F], bSheetAccId: String, inStmtAccId: String)
    extends Service[F, Account] {

  def create(item: Account): F[Int] = SQL.Account.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Account.delete(id, company).run.transact(transactor)

  def list(from: Int, until: Int, company: String): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.list(company))
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def getBy(id: String, company: String): F[Option[Account]] =
    SQL.Account
      .getBy(id, company)
      .map(Account.apply)
      .option
      .transact(transactor)

  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.findSome(company, model: _*))
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Account]] =
    paginate(until - from, from)(SQL.Account.getByModelId(modelid, company))
      .map(Account.apply)
      .to[List]
      .transact(transactor)

  def update(model: Account, company: String): F[List[Int]] =
    getXX(SQL.Account.update, List(model), company).sequence.transact(transactor)

  def getBalances(accId: String, fromPeriod: Int, toPeriod: Int, company: String): F[Data] =
    (for {
      list <- SQL.Account.listX(fromPeriod, toPeriod, company).map(Account.apply).to[List]
      period = fromPeriod.toString.slice(0, 4).concat("00").toInt
      pacs <- SQL.PeriodicAccountBalance.find4Period(company, List(period, period)).to[List]
    } yield {
      //val acc = Account.consolidate("9900", list.filterNot((acc => acc.balance == 0 && acc.subAccounts.size == 0)))
      val acc = Account.consolidate(accId, list, pacs)
      val data = Account.consolidateData(acc)
      data
    }).transact(transactor)

  def closePeriod(fromPeriod: Int, toPeriod: Int, company: String): F[List[Int]] =
    (for {
      pacs <- SQL.PeriodicAccountBalance.findBalance4Period(company, List(fromPeriod, toPeriod)).to[List]
      allAccounts <- SQL.Account.list(company).map(Account.apply).to[List]
      currentYear = fromPeriod.toString.slice(0, 4).toInt
      currentPeriod = currentYear.toString.concat("00").toInt
      nextPeriod = (currentYear + 1).toString.concat("00").toInt
      initial <- SQL.PeriodicAccountBalance.find4Period(company, List(currentPeriod, currentPeriod)).to[List]
      list = Account.flattenTailRec(Set(Account.withChildren(inStmtAccId, allAccounts)))
      initpacList = (pacs ++ initial)
        .groupBy(_.account)
        .map({ case (_, v) => v.combineAll(pacMonoid) })
        .toList

      filteredList = initpacList.filterNot(x => {
        list.find(_.id == x.account) match {
          case Some(_) => true
          case None => false
        }
      })

      pacList = filteredList
        .filterNot(x => (x.dbalance == 0 || x.cbalance == 0))
        .map(pac => {
          allAccounts.find(_.id == pac.account) match {
            case Some(acc) =>
              if (acc.isDebit)
                pac
                  .copy(id = PeriodicAccountBalance.createId(nextPeriod, acc.id), period = nextPeriod)
                  .idebiting(pac.debit - pac.icredit - pac.credit)
                  .copy(debit = 0)
                  .copy(icredit = 0)
                  .copy(credit = 0)
              else
                pac
                  .copy(id = PeriodicAccountBalance.createId(nextPeriod, acc.id), period = nextPeriod)
                  .icrediting(pac.credit - pac.idebit - pac.debit)
                  .copy(credit = 0)
                  .copy(idebit = 0)
                  .copy(debit = 0)
            case None => pac
          }
        })
      pac_created <- pacList.traverse(SQL.PeriodicAccountBalance.create(_).run)
    } yield pac_created).transact(transactor)
}
case class ArticleService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Article] {
  def create(item: Article): F[Int] = SQL.Article.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Article.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Article]] =
    SQL.Article.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.getByModelId(modelid, company)).to[List].transact(transactor)

  def update(model: Article, company: String): F[List[Int]] =
    getXX(SQL.Article.update, List(model), company).sequence.transact(transactor)
}

case class CustomerService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Customer] {
  def create(item: Customer): F[Int] = SQL.Customer.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Customer.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Customer]] =
    SQL.Customer.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Customer]] =
    paginate(until - from, from)(SQL.Customer.getByModelId(modelid, company)).to[List].transact(transactor)

  def update(model: Customer, company: String): F[List[Int]] =
    getXX(SQL.Customer.update, List(model), company).sequence.transact(transactor)
}
case class SupplierService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Supplier] {
  def create(item: Supplier): F[Int] = SQL.Supplier.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Supplier.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Supplier]] =
    SQL.Supplier.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Supplier]] =
    paginate(until - from, from)(SQL.Supplier.getByModelId(modelid, company)).to[List].transact(transactor)

  def update(model: Supplier, company: String): F[List[Int]] =
    getXX(SQL.Supplier.update, List(model), company).sequence.transact(transactor)

}
case class PeriodicAccountBalanceService[F[_]: Sync](
  transactor: Transactor[F]
) extends Service[F, PeriodicAccountBalance] {

  def create(item: PeriodicAccountBalance): F[Int] = SQL.PeriodicAccountBalance.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] =
    SQL.PeriodicAccountBalance.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[PeriodicAccountBalance]] =
    SQL.PeriodicAccountBalance.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(SQL.PeriodicAccountBalance.getByModelId(modelid, company))
      .to[List]
      .transact(transactor)

  def update(model: PeriodicAccountBalance, company: String): F[List[Int]] =
    getXX(SQL.PeriodicAccountBalance.update, List(model), company).sequence.transact(transactor)
}

case class RoutesService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Routes] {
  def create(item: Routes): F[Int] = SQL.Routes.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Routes.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Routes]] = SQL.Routes.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Routes]] =
    paginate(until - from, from)(SQL.Routes.getByModelId(modelid, company)).to[List].transact(transactor)

  def update(model: Routes, company: String): F[List[Int]] =
    getXX(SQL.Routes.update, List(model), company).sequence.transact(transactor)
}
case class VatService[F[_]: Sync](transactor: Transactor[F]) extends Service[F, Vat] {
  def create(item: Vat): F[Int] = SQL.Vat.create(item).run.transact(transactor)
  def delete(id: String, company: String): F[Int] = SQL.Vat.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Vat]] = SQL.Vat.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Vat]] =
    paginate(until - from, from)(SQL.Vat.getByModelId(modelid, company)).to[List].transact(transactor)

  def update(model: Vat, company: String): F[List[Int]] =
    getXX(SQL.Vat.update, List(model), company).sequence.transact(transactor)
}
case class FinancialsTransactionService[F[_]: Sync](transactor: Transactor[F])
    extends Service[F, FinancialsTransaction] {

  import com.kabasoft.iws.domain.{FinancialsTransaction, FinancialsTransactionDetails => FTDetails}
  import com.kabasoft.iws.domain.PeriodicAccountBalance.pacMonoid

  implicit val FinancialsTransactionDetailsMonoid: Monoid[FTDetails] =
    new Monoid[FTDetails] {
      def empty =
        FTDetails(0, 0, "", true, "", BigDecimal(0), Instant.now(), "", "EUR", "1000")

      def combine(m1: FTDetails, m2: FTDetails) = m2.copy(amount = m2.amount.+(m1.amount))
    }

  def create(item: FinancialsTransaction): F[Int] = SQL.FinancialsTransaction.create(item).run.transact(transactor)

  def delete(id: String, company: String): F[Int] =
    SQL.FinancialsTransaction.delete(id, company).run.transact(transactor)

  def update(model: FinancialsTransaction, company: String): F[List[Int]] = {

    def insertPredicate(line: FinancialsTransactionDetails) = line.lid == -1L

    def deletePredicate(line: FinancialsTransactionDetails) = line.transid == -2L

    val splitted = model.lines.partition(insertPredicate(_))
    val splitted2 = splitted._2.partition(deletePredicate(_))
    println("model: " + model)
    println("splitted1: " + splitted)
    println("splitted2: " + splitted2)
    val newLines = splitted._1
    val deletedLineIds = splitted2._1.map(line => line.id)
    val oldLines = splitted2._2
    println("INSERT->" + newLines);
    println("DELETE->" + deletedLineIds);
    println("OLD->" + oldLines);
    val result: List[ConnectionIO[Int]] =
      getXX(SQL.FinancialsTransactionDetails.update, oldLines, company) ++
        getXX(SQL.FinancialsTransactionDetails.delete, deletedLineIds, company) ++
        getXX(SQL.FinancialsTransactionDetails.create, newLines) ++
        List(getX(SQL.FinancialsTransaction.update, model, company))

    result.sequence.transact(transactor)

  }

  def list(from: Int, until: Int, company: String): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.list(company))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def getBy(id: String, company: String): F[Option[FinancialsTransaction]] =
    SQL.FinancialsTransaction
      .getBy(id, company)
      .to[List]
      .map(FinancialsTransaction.apply)
      .map(_.headOption)
      .transact(transactor)

  def findSome(from: Int, until: Int, company: String, model: String*): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.findSome(company, model: _*))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(SQL.FinancialsTransaction.getByModelId(modelid, company))
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def postAll(models: List[FinancialsTransaction], company: String): F[List[Int]] =
    (for {
      all <- models.filter(_.posted == false).traverse(debitOrCreditPACAll(_, company))
    } yield all.flatten).transact(transactor)

  def post(model: FinancialsTransaction, company: String): F[List[Int]] = postAll(List(model), company)

  private[this] def debitOrCreditPACAll(model: FinancialsTransaction, company: String): ConnectionIO[List[Int]] =
    (
      for {
        pac <- getIds(model: FinancialsTransaction).traverse(SQL.PeriodicAccountBalance.getBy(_, company).option)
        oldRecords: List[DPAC] = getOldPac(pac, model)
        newRecords: List[DPAC] = getNewPac(pac, model)
        journalEntries: List[DJournal] = newJournalEntries(model, oldRecords ::: newRecords)
        pac_created <- newRecords.traverse(SQL.PeriodicAccountBalance.create(_).run)
        pac_updated <- oldRecords.traverse(SQL.PeriodicAccountBalance.update(_, company).run)
        trans_posted <- List(model.copy(posted = true).copy(postingdate = Instant.now()))
          .traverse(SQL.FinancialsTransaction.update2(_, company).run)
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
    println("newRecords: " + newRecords)
    val grouped: Iterable[DPAC] = newRecords
      .groupBy(_.id)
      .map({ case (_, v) => v.combineAll })
    val result = grouped.toList
    println("result: " + result)
    result
  }

  private[this] def getOldPac(pacList: List[Option[DPAC]], model: FinancialsTransaction): List[DPAC] = {
    val pacs: List[DPAC] = getAndDebitCreditOldPacs(pacList, model.getPeriod, model.lines)

    val result: List[DPAC] = pacs
      .groupBy(_.id)
      .map({ case (_, v) => v.combineAll })
      .toSet
      .toList
    println("result: " + result)
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
  def delete(id: String, company: String): F[Int] = SQL.Journal.delete(id, company).run.transact(transactor)
  def list(from: Int, until: Int, company: String): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.list(company)).to[List].transact(transactor)
  def getBy(id: String, company: String): F[Option[Journal]] =
    SQL.Journal.getBy(id, company).option.transact(transactor)
  def findSome(from: Int, until: Int, company: String, model: String*): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.findSome(company, model: _*)).to[List].transact(transactor)
  def getByModelId(modelid: Int, from: Int, until: Int, company: String): F[List[Journal]] =
    paginate(until - from, from)(SQL.Journal.getByModelId(modelid, company)).to[List].transact(transactor)
  def update(model: Journal, company: String): F[List[Int]] =
    getXX(SQL.Journal.update, List(model), company).sequence.transact(transactor)

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

DROP SEQUENCE IF EXISTS bankstatement_id_seq ;
CREATE SEQUENCE bankstatement_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807
   START 38963
   CACHE 1;
alter sequence bankstatement_id_seq owner to postgres;

CREATE TABLE USERS (
  ID BIGSERIAL PRIMARY KEY,
  USER_NAME VARCHAR NOT NULL UNIQUE,
  FIRST_NAME VARCHAR NOT NULL,
  LAST_NAME VARCHAR NOT NULL,
  EMAIL VARCHAR NOT NULL,
  HASH VARCHAR NOT NULL,
  PHONE VARCHAR NOT NULL,
  ROLE VARCHAR NOT NULL DEFAULT 'Customer'
);
CREATE TABLE JWT (
  ID VARCHAR PRIMARY KEY,
  JWT VARCHAR NOT NULL,
  -- BIGINT instead of BIGSERIAL
  IDENTITY BIGINT NOT NULL REFERENCES USERS (ID) ON DELETE CASCADE,
  EXPIRY TIMESTAMP NOT NULL,
  LAST_TOUCHED TIMESTAMP
);

 */
