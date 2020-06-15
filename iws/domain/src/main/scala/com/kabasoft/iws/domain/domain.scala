package com.kabasoft.iws.domain

import java.time.Instant
import java.time.temporal.ChronoField
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.ZoneId

import cats._
import cats.implicits._
import com.kabasoft.iws.domain.FinancialsTransaction.FinancialsTransaction_Type2
import com.kabasoft.iws.domain.Account.{Balance_Type}
import com.kabasoft.iws.domain.common._

import scala.annotation.tailrec

object common {
  type Amount = scala.math.BigDecimal

  def getMonthAsString(month: Int): String =
    if (month <= 9) {
      "0".concat(month.toString)
    } else month.toString
  def getYear(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.of("UTC+1")).getYear
  def getMonthAsString(instant: Instant): String =
    getMonthAsString(LocalDateTime.ofInstant(instant, ZoneId.of("UTC+1")).getMonth.getValue)
  def getPeriod(instant: Instant) = {
    val year = LocalDateTime.ofInstant(instant, ZoneId.of("UTC+1")).getYear
    year.toString.concat(getMonthAsString(instant)).toInt
  }

  val zero = BigDecimal(0)
  implicit val parentXAccountMonoid: Monoid[Account] =
    new Monoid[Account] {
      def empty =
        new Account("", "", "", Instant.now(), Instant.now(), Instant.now(), "1000", 9, "", false, false, "EUR")

      def combine(m1: Account, m2: Account) =
        m2.copy(id = m2.account)
          .idebiting(m1.idebit)
          .icrediting(m1.icredit)
          .debiting(m1.debit)
          .crediting(m1.credit)
    }
  implicit val baseDataMonoid: Monoid[BaseData] =
    new Monoid[BaseData] {
      def empty =
        BaseData(Account("", "", "", Instant.now(), Instant.now(), Instant.now(), "1000", 9, "", false, false, ""))
      def combine(m1: BaseData, m2: BaseData) =
        m2.idebiting(m1.idebit)
          .icrediting(m1.icredit)
          .debiting(m1.debit)
          .crediting(m1.credit)

    }
  implicit val dataMonoid: Monoid[Data] =
    new Monoid[Data] {
      def empty =
        Data(
          BaseData(Account("", "", "", Instant.now(), Instant.now(), Instant.now(), "1000", 9, "", false, false, ""))
        )

      def combine(m1: Data, m2: Data) = Data(m1.data.combine(m2.data))

    }
  val balanceMonoid: Monoid[Balance_Type] =
    new Monoid[Balance_Type] {
      def empty = new Balance_Type(zero, zero, zero, zero)
      def combine(m1: Balance_Type, m2: Balance_Type) =
        new Balance_Type(m1._1 + m2._1, m1._2 + m2._2, m1._3 + m2._3, m1._4 + m2._4)

    }
}
case class MasterfileId(value: String) extends AnyVal
sealed trait IWS {
  def id: String
}
object Currency extends Enumeration {
  type ccy = Value
  val CHF, CNY, EUR, DEM, GNF, JPY, USD, XOF = Value
}
final case class Masterfile(id: String, name: String, description: String, modelid: Int, parent: String) extends IWS
final case class Routes(id: String, name: String, description: String, modelid: Int, component: String) extends IWS
final case class Article(
  id: String,
  name: String = "",
  description: String = "",
  modelid: Int,
  parent: String = "",
  price: Amount = 0.0,
  stocked: Boolean = false
) extends IWS
final case class Account(
  id: String,
  name: String,
  description: String,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  company: String,
  modelid: Int = 9,
  account: String = "",
  isDebit: Boolean,
  balancesheet: Boolean,
  currency: String,
  idebit: BigDecimal = BigDecimal(0),
  icredit: BigDecimal = BigDecimal(0),
  debit: BigDecimal = BigDecimal(0),
  credit: BigDecimal = BigDecimal(0),
  subAccounts: Set[Account] = Nil.toSet
  //parent: Option[Account] = None
) extends IWS {
  def debiting(amount: BigDecimal) = copy(debit = debit.+(amount))

  def crediting(amount: BigDecimal) = copy(credit = credit.+(amount))

  def idebiting(amount: BigDecimal) = copy(idebit = idebit.+(amount))

  def icrediting(amount: BigDecimal) = copy(icredit = icredit.+(amount))

  def fdebit = debit + idebit

  def fcredit = credit + icredit

  def dbalance = fdebit - fcredit

  def cbalance = fcredit - fdebit

  def balance = if (isDebit) dbalance else cbalance

  def incomeStmt = account.isEmpty

  def add(acc: Account): Account =
    copy(subAccounts = subAccounts + acc);

  def addAll(accSet: Set[Account]) =
    copy(subAccounts = subAccounts ++ accSet)

  def updateBalance(acc: Account): Account =
    idebiting(acc.idebit).icrediting(acc.icredit).debiting(acc.debit).crediting(acc.credit)

  def childBalances: BigDecimal = subAccounts.toList.combineAll.balance

}

object Account {
  type Balance_Type = (BigDecimal, BigDecimal, BigDecimal, BigDecimal)
  type Account_Type = (
    String,
    String,
    String,
    Instant,
    Instant,
    Instant,
    String,
    Int,
    String,
    Boolean,
    Boolean,
    String,
    BigDecimal,
    BigDecimal,
    BigDecimal,
    BigDecimal
  )
  def apply(acc: Account_Type): Account =
    Account(
      acc._1,
      acc._2,
      acc._3,
      acc._4,
      acc._5,
      acc._6,
      acc._7,
      acc._8,
      acc._9,
      acc._10,
      acc._11,
      acc._12,
      acc._13,
      acc._14,
      acc._15,
      acc._16,
      Nil.toSet
    )
  val dummy = Account("", "", "", Instant.now(), Instant.now(), Instant.now(), "1000", 9, "", false, false, "")
  def group(accounts: List[Account]): List[Account] =
    accounts
      .groupBy(_.id)
      .map({
        case (k, v: List[Account]) => v.combineAll.copy(id = k)
      })
      .toList

  def addSubAccounts(account: Account, accMap: Map[String, List[Account]]): Account =
    accMap.get(account.id) match {
      case Some(acc) =>
        account.copy(subAccounts = account.subAccounts ++ (group(acc).map(x => addSubAccounts(x, accMap))))
      case None =>
        if (account.subAccounts.size > 0)
          account.copy(
            subAccounts = account.subAccounts ++ (group(account.subAccounts.toList).map(x => addSubAccounts(x, accMap)))
          )
        else account
    }
  def getInitialDebitCredit(accId: String, pacs: List[PeriodicAccountBalance], side: Boolean): BigDecimal =
    pacs.find(x => x.account == accId) match {
      case Some(acc) => if (side) acc.idebit else acc.icredit
      case None => BigDecimal(0)
    }
  def getAllSubBalances(account: Account, pacs: List[PeriodicAccountBalance]): Account =
    account.subAccounts.toList match {
      case Nil => account.copy(idebit = getInitialDebitCredit(account.id, pacs, true),
                               icredit = getInitialDebitCredit(account.id, pacs, false))
      case s :: rest =>
        val sub = account.subAccounts.map(acc => getAllSubBalances(acc, pacs))
        val subALl = sub.toList.combineAll
        account
          .idebiting(subALl.idebit)
          .icrediting(subALl.icredit)
          .debiting(subALl.debit)
          .crediting(subALl.credit)
          .copy(subAccounts = sub)
    }
  def wrapAsData(account: Account): Data =
     account.subAccounts.toList match {
         case Nil => Data(BaseData(account))
         case s :: rest =>
           Data(BaseData(account)).copy(children = account.subAccounts.toList.map(wrapAsData))
     }


  def consolidateData(acc: Account): Data =
    List(acc).map(wrapAsData) match {
      case Nil => Data(BaseData(Account.dummy))
      case account :: rest => account
    }

  def consolidate(accId: String, accList: List[Account], pacs: List[PeriodicAccountBalance]): Account =
    accList.find(x => x.id == accId) match {
      case Some(acc) =>
        val accMap = accList.groupBy(_.account)
        val x: Account = List(acc)
          .foldMap(addSubAccounts(_, accMap))(parentXAccountMonoid)
          .copy(id = accId)
        val y = List(x).foldMap(getAllSubBalances(_, pacs))(parentXAccountMonoid)
        y.copy(id = acc.id)
      case None => Account.dummy
    }
}
final case class BaseData(
  id: String,
  name: String,
  description: String,
  modelId: Int = 19,
  isDebit: Boolean,
  balancesheet: Boolean,
  idebit: BigDecimal,
  icredit: BigDecimal,
  debit: BigDecimal,
  credit: BigDecimal,
  currency: String,
  company: String
) {
  def debiting(amount: BigDecimal) = copy(debit = debit.+(amount))
  def crediting(amount: BigDecimal) = copy(credit = credit.+(amount))
  def idebiting(amount: BigDecimal) = copy(idebit = idebit.+(amount))
  def icrediting(amount: BigDecimal) = copy(icredit = icredit.+(amount))
  def fdebit = debit + idebit
  def fcredit = credit + icredit
  def dbalance = fdebit - fcredit
  def cbalance = fcredit - fdebit
  def balance = if (isDebit) dbalance else cbalance

}
object BaseData {
  def apply(acc: Account): BaseData =
    BaseData(
      acc.id,
      acc.name,
      acc.description,
      19,
      acc.isDebit,
      acc.balancesheet,
      acc.idebit,
      acc.icredit,
      acc.debit,
      acc.credit,
      acc.currency,
      acc.company
    )
}
final case class Data(data: BaseData, children: Seq[Data] = Nil)
final case class Children(data: Data)
final case class PeriodicAccountBalance private (
  id: String,
  account: String,
  period: Int,
  idebit: BigDecimal,
  icredit: BigDecimal,
  debit: BigDecimal,
  credit: BigDecimal,
  company: String,
  currency: String,
  modelid: Int = PeriodicAccountBalance.MODELID
) extends IWS {
  def debiting(amount: BigDecimal) = copy(debit = debit.+(amount))
  def crediting(amount: BigDecimal) = copy(credit = credit.+(amount))
  def idebiting(amount: BigDecimal) = copy(idebit = idebit.+(amount))
  def icrediting(amount: BigDecimal) = copy(icredit = icredit.+(amount))
  def fdebit = debit + idebit
  def fcredit = credit + icredit
  def dbalance = fdebit - fcredit
  def cbalance = fcredit - fdebit

}

object PeriodicAccountBalance {
  val MODELID = 106
  def apply(
    account: String,
    period: String,
    idebit: BigDecimal,
    icredit: BigDecimal,
    debit: BigDecimal,
    credit: BigDecimal,
    company: String,
    currency: String
  ) =
    new PeriodicAccountBalance(
      period.concat(account),
      account,
      period.toInt,
      idebit,
      icredit,
      debit,
      credit,
      company,
      currency
    )
  def apply(
    account: String,
    period: Int,
    idebit: BigDecimal,
    icredit: BigDecimal,
    debit: BigDecimal,
    credit: BigDecimal,
    company: String,
    currency: String
  ) =
    new PeriodicAccountBalance(
      period.toString.concat(account),
      account,
      period.toInt,
      idebit,
      icredit,
      debit,
      credit,
      company,
      currency
    )
  def apply(
    account: String,
    period: String,
    idebit: String,
    icredit: String,
    debit: String,
    credit: String,
    company: String,
    currency: String
  ) =
    new PeriodicAccountBalance(
      period.concat(account),
      account,
      period.toInt,
      BigDecimal(idebit.replace(".0000", ".00")),
      BigDecimal(icredit.replace(".0000", ".00")),
      BigDecimal(debit.replace(".0000", ".00")),
      BigDecimal(credit.replace(".0000", ".00")),
      company,
      currency
    )
  def init(paccs: List[PeriodicAccountBalance]) =
    paccs.foreach(
      _.copy(idebit = BigDecimal(0), debit = BigDecimal(0), icredit = BigDecimal(0), credit = BigDecimal(0))
    )
  def createId(period: Int, accountId: String) = period.toString.concat(accountId)

}
final case class Bank(
  id: String,
  name: String = "",
  description: String = "",
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  modelid: Int = 11,
  company: String
) extends IWS
final case class CostCenter(
  id: String,
  name: String = "",
  description: String = "",
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  modelid: Int = 6,
  account: String = "",
  company: String
) extends IWS

final case class Vat(
  id: String,
  name: String = "",
  description: String = "",
  percent: BigDecimal,
  inputVatAccount: String,
  outputVatAccount: String,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  company: String,
  modelid: Int = 6
) extends IWS
final case class Supplier(
  id: String,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zip: String,
  country: String,
  phone: String,
  email: String,
  account: String,
  oaccount: String,
  iban: String,
  vatcode: String,
  company: String,
  modelid: Int = 3,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now()
) extends IWS
final case class Customer(
  id: String,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zip: String,
  country: String,
  phone: String,
  email: String,
  account: String,
  oaccount: String,
  iban: String,
  vatcode: String,
  company: String,
  modelid: Int = 3,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now()
) extends IWS
final case class FinancialsTransactionDetails(
  lid: Long,
  transid: Long,
  account: String,
  side: Boolean,
  oaccount: String,
  amount: BigDecimal,
  duedate: Instant = Instant.now(),
  text: String,
  currency: String,
  // modelId: Int,
  company: String
) extends IWS {
  def id = lid.toString

}
object FinancialsTransactionDetails {
  type FinancialsTransactionDetails_Type =
    (Long, Long, String, Boolean, String, String, Instant, String, String, String)
  def apply(tr: FinancialsTransactionDetails_Type): FinancialsTransactionDetails =
    new FinancialsTransactionDetails(
      tr._1,
      tr._2,
      tr._3,
      tr._4,
      tr._5,
      BigDecimal(tr._6.replace(",", "").replace("$", "")),
      tr._7,
      tr._8,
      tr._9,
      tr._10
    )
  def apply(tr: FinancialsTransaction_Type2): FinancialsTransactionDetails =
    new FinancialsTransactionDetails(
      tr._15.toLong,
      tr._1.toLong,
      tr._16,
      tr._17,
      tr._18,
      BigDecimal(tr._19.replace(",", "").replace("$", "")),
      tr._20,
      tr._21,
      tr._22,
      tr._11
    )
}
final case class FinancialsTransaction(
  tid: Long,
  oid: Long,
  costcenter: String,
  account: String,
  transdate: Instant = Instant.now(),
  enterdate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  period: Int = common.getPeriod(Instant.now()),
  posted: Boolean = false,
  modelid: Int,
  company: String,
  text: String = "",
  typeJournal: Int = 0,
  file_content: Int = 0,
  lines: List[FinancialsTransactionDetails] = Nil
) extends IWS {
  def id = tid.toString
  def month: String = common.getMonthAsString(transdate)
  def year: Int = common.getYear(transdate)
  def getPeriod = common.getPeriod(transdate)

}

object FinancialsTransaction {
  type FinancialsTransaction_Type =
    (Long, Long, String, String, Instant, Instant, Instant, Int, Boolean, Int, String, String, Int, Int)
  type FinancialsTransaction_Type2 =
    (
      Long,
      Long,
      String,
      String,
      Instant,
      Instant,
      Instant,
      Int,
      Boolean,
      Int,
      String,
      String,
      Int,
      Int,
      Long,
      String,
      Boolean,
      String,
      String,
      Instant,
      String,
      String
    )
  def apply(tr: FinancialsTransaction_Type): FinancialsTransaction =
    FinancialsTransaction(
      tr._1,
      tr._2,
      tr._3,
      tr._4,
      tr._5,
      tr._6,
      tr._7,
      tr._8,
      tr._9,
      tr._10,
      tr._11,
      tr._12,
      tr._13,
      tr._14,
      Nil
    )
  def apply(transactions: List[FinancialsTransaction_Type2]): List[FinancialsTransaction] =
    transactions
      .groupBy(
        rc => (rc._1, rc._2, rc._3, rc._4, rc._5, rc._6, rc._7, rc._8, rc._9, rc._10, rc._11, rc._12, rc._13, rc._14)
      )
      .map({
        case (k, v) =>
          new FinancialsTransaction(
            k._1,
            k._2,
            k._3,
            k._4,
            k._5,
            k._6,
            k._7,
            k._8,
            k._9,
            k._10,
            k._11,
            k._12,
            k._13,
            k._14
          ).copy(lines = v.filter(p => p._15 != -1).map(FinancialsTransactionDetails.apply))
      })
      .toList

  def apply(x: FinancialsTransaction_Type2) =
    new FinancialsTransaction(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14)
      .copy(lines = List(FinancialsTransactionDetails.apply(x)))

}

final case class Journal(
  id: Long,
  transid: Long,
  oid: Long,
  account: String,
  oaccount: String,
  transdate: Instant,
  postingdate: Instant,
  enterdate: Instant,
  period: Int,
  amount: BigDecimal,
  idebit: BigDecimal,
  debit: BigDecimal,
  icredit: BigDecimal,
  credit: BigDecimal,
  currency: String,
  side: Boolean,
  text: String = "",
  month: Int,
  year: Int,
  company: String,
  typeJournal: Int = 0,
  file_content: Int = 0,
  modelid: Int
)
object Journal {

  def apply(x: Journal) =
    new Journal(
      x.id,
      x.transid,
      x.oid,
      x.account,
      x.oaccount,
      x.transdate,
      x.postingdate,
      x.enterdate,
      x.period,
      x.amount,
      x.idebit,
      x.icredit,
      x.debit,
      x.credit,
      x.currency,
      x.side,
      x.text,
      x.month,
      x.year,
      x.company,
      x.typeJournal,
      x.file_content,
      x.modelid
    )
}

final case class BankStatement(
  id: Long,
  depositor: String,
  postingdate: String,
  valuedate: String,
  postingtext: String,
  purpose: String,
  beneficiary: String,
  accountno: String,
  bankCode: String,
  amount: String,
  currency: String,
  info: String,
  company: String,
  companyIban: String,
  posted: Boolean,
  modelid: Int = 18
)
