package com.kabasoft.iws.domain
import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import cats._
import cats.implicits._
import com.kabasoft.iws.domain.Account.Balance_Type
import com.kabasoft.iws.domain.FinancialsTransaction.FinancialsTransaction_Type2
//import com.kabasoft.iws.domain.Account.Balance_Typ
import com.kabasoft.iws.domain.common._

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
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

  val dummyCustomer = Customer(
    "-1",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy"
  )
  val dummySupplier = Supplier(
    "-1",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy",
    "dummy"
  )
}
trait build {
  def from [A] (s:String ):A
}
case class Param(id: Long, modelid: Int)
case class MasterfileId(value: String) extends AnyVal
sealed trait IWS {
  def id: String
}
object Currency extends Enumeration {
  type ccy = Value
  val CHF, CNY, EUR, DEM, GNF, JPY, USD, XOF = Value
}
final case class Masterfile(
  id: String,
  name: String,
  description: String,
  modelid: Int,
  parent: String,
  company: String
) extends IWS
final case class Routes(id: String, name: String, description: String, modelid: Int, component: String, company: String)
    extends IWS
final case class Article(
  id: String,
  name: String = "",
  description: String = "",
  modelid: Int,
  parent: String = "",
  price: Amount = 0.0,
  stocked: Boolean = false,
  company: String
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
  currency: String = "EUR ",
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

  def add(acc: Account): Account =
    copy(subAccounts = subAccounts + acc);

  def addAll(accSet: Set[Account]) =
    copy(subAccounts = subAccounts ++ accSet)

  def updateBalance(acc: Account): Account =
    idebiting(acc.idebit).icrediting(acc.icredit).debiting(acc.debit).crediting(acc.credit)

  def childBalances: BigDecimal = subAccounts.toList.combineAll.balance

  def getChildren(): Set[Account] = subAccounts.toList match {
    case Nil => Set(copy(id = id))
    case x :: xs => Set(x) ++ xs.flatMap(_.getChildren())
  }
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
  val dummy = Account("", "", "", Instant.now(), Instant.now(), Instant.now(), "1000", 9, "", false, false, "EUR")
  def group(accounts: List[Account]): List[Account] =
    accounts
      .groupBy(_.id)
      .map({
        case (k, v: List[Account]) => v.combineAll.copy(id = k)
      })
      .toList

  def removeSubAccounts(account: Account): Account =
    account.subAccounts.toList match {
      case Nil => account
      case rest @ _ =>
        val sub = account.subAccounts.filterNot((acc => acc.balance == 0 && acc.subAccounts.size == 0))
        if (account.subAccounts.size > 0)
          account.copy(subAccounts = sub.map(removeSubAccounts))
        else account
    }
  def addSubAccounts2(account: Account, accMap: Map[String, List[Account]]): Account =
    accMap.get(account.id) match {
      case Some(acc) => {
        val x = account.subAccounts ++ acc.map(x => addSubAccounts(x, accMap))
        account.copy(subAccounts = x)
      }
      case None =>
        if (account.subAccounts.size > 0)
          account.copy(
            subAccounts = account.subAccounts ++ account.subAccounts.toList.map(x => addSubAccounts(x, accMap))
          )
        else account
    }
  def addSubAccounts(account: Account, accMap: Map[String, List[Account]]): Account =
    accMap.get(account.id) match {
      case Some(accList) =>
        //account.copy(subAccounts = account.subAccounts ++ (group(accList).map(x => addSubAccounts(x, accMap))))
        account.copy(subAccounts = account.subAccounts ++ accList.map(x => addSubAccounts(x, accMap)))
      case None =>
        if (account.subAccounts.size > 0)
          account.copy(
            subAccounts = account.subAccounts ++ account.subAccounts.toList.map(x => addSubAccounts(x, accMap))
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
      case Nil =>
        account.copy(
          idebit = getInitialDebitCredit(account.id, pacs, true),
          icredit = getInitialDebitCredit(account.id, pacs, false)
        )
      case rest @ _ =>
        val sub = account.subAccounts.map(acc => getAllSubBalances(acc, pacs))
        val subALl = sub.toList.combineAll(parentXAccountMonoid)
        account
          .idebiting(subALl.idebit)
          .icrediting(subALl.icredit)
          .debiting(subALl.debit)
          .crediting(subALl.credit)
          .copy(subAccounts = sub)
    }

  def unwrapDataTailRec(account: Account): List[Account] = {
    //@tailrec
    def unwrapData(res: List[Account]): List[Account] =
      res.flatMap(
        acc =>
          acc.subAccounts.toList match {
            case Nil => if (acc.balance == 0.0 && acc.subAccounts.isEmpty) List.empty[Account] else List(acc)
            case (head: Account) :: tail => List(acc, head) ++ unwrapData(tail)
          }
      )
    unwrapData(account.subAccounts.toList)
  }

  def wrapAsData(account: Account): Data =
    account.subAccounts.toList match {
      case Nil => Data(BaseData(account))
      case rest @ _ =>
        Data(BaseData(account)).copy(children = account.subAccounts.toList.map(wrapAsData))
    }

  def consolidateData(acc: Account): Data =
    List(acc).map(wrapAsData) match {
      case Nil => Data(BaseData(Account.dummy))
      case account :: _ => account
    }

  def consolidate(accId: String, accList: List[Account], pacs: List[PeriodicAccountBalance]): Account = {
    val accMap = accList.groupBy(_.account)
    accList.find(x => x.id == accId) match {
      case Some(acc) =>
        val x: Account = addSubAccounts(acc, accMap) //List(acc)
        val y = getAllSubBalances(x, pacs)
        val z = removeSubAccounts(y.copy(id = acc.id))
        z
      case None => Account.dummy
    }
  }

  def withChildren(accId: String, accList: List[Account]): Account =
    accList.find(x => x.id == accId) match {
      case Some(acc) =>
        List(acc)
          .foldMap(addSubAccounts2(_, accList.groupBy(_.account)))(parentXAccountMonoid)
          .copy(id = accId)
      case None => Account.dummy
    }
  def flattenTailRec(ls: Set[Account]): Set[Account] = {
    @tailrec
    def flattenR(res: List[Account], rem: List[Account]): List[Account] = rem match {
      case Nil => res
      case (head: Account) :: tail => flattenR(res ++ List(head), head.subAccounts.toList ++ tail)
    }
    flattenR(List.empty[Account], ls.toList).toSet
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
//final case class Children(data: Data)
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
      period,
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

  implicit val pacMonoid: Monoid[PeriodicAccountBalance] =
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
}
final case class Module(
  id: String,
  name: String = "",
  description: String = "",
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  modelid: Int = 300,
  company: String
) extends IWS
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
final case class BankAccount(iban: String, bic: String, owner: String, company: String, modelid: Int = 12) extends IWS {
  def id: String = iban
  def name: String = owner

}
object BankAccount {
  def apply(iban: String, bic: String, owner: String, company: String) =
    new BankAccount(iban, bic, owner, company)
}
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
  modelid: Int = 1,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  bankaccounts: List[BankAccount] = Nil
) extends IWS
object Supplier {
  type Supplier_Type = (
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    Int,
    Instant,
    Instant,
    Instant
  )
  def apply(s: Supplier_Type): Supplier =
    new Supplier(
      s._1,
      s._2,
      s._3,
      s._4,
      s._5,
      s._6,
      s._7,
      s._8,
      s._9,
      s._10,
      s._11,
      s._12,
      s._13,
      s._14,
      s._15,
      s._16,
      s._17,
      s._18,
      s._19,
      Nil
    )
  def apply(l: Option[Supplier_Type]): Option[Supplier] = l.map(s => apply(s))
  def apply(l: List[Supplier_Type]): List[Supplier] = l.map(s => apply(s))
}
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
  postingdate: Instant = Instant.now(),
  bankaccounts: List[BankAccount] = Nil
) extends IWS
object Customer {
  type Customer_Type = (
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    Int,
    Instant,
    Instant,
    Instant
  )
  def apply(s: Customer_Type): Customer =
    new Customer(
      s._1,
      s._2,
      s._3,
      s._4,
      s._5,
      s._6,
      s._7,
      s._8,
      s._9,
      s._10,
      s._11,
      s._12,
      s._13,
      s._14,
      s._15,
      s._16,
      s._17,
      s._18,
      s._19,
      Nil
    )
  def apply(l: Option[Customer_Type]): Option[Customer] = l.map(c => apply(c))
  def apply(l: List[Customer_Type]): List[Customer] = l.map(c => apply(c))

}
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

  val EMPTY = FinancialsTransactionDetails(0, 0, "", true, "", BigDecimal(0), Instant.now(), "", "EUR", "1000")
  implicit val monoid: Monoid[FinancialsTransactionDetails] =
    new Monoid[FinancialsTransactionDetails] {
      def empty = EMPTY
      def combine(m1: FinancialsTransactionDetails, m2: FinancialsTransactionDetails) =
        m2.copy(amount = m2.amount.+(m1.amount))
    }
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
  //,copyFlag: Boolean = false
) extends IWS {
  def id = tid.toString
  def month: String = common.getMonthAsString(transdate)
  def year: Int = common.getYear(transdate)
  def getPeriod = common.getPeriod(transdate)
  def total = lines.reduce((l1, l2) => l2.copy(amount = l2.amount + l1.amount))

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
  bid: Long,
  depositor: String,
  postingdate: Instant,
  valuedate: Instant,
  postingtext: String,
  purpose: String,
  beneficiary: String,
  accountno: String,
  bankCode: String,
  amount: BigDecimal,
  currency: String,
  info: String,
  company: String,
  companyIban: String,
  posted: Boolean = false,
  modelid: Int = 18
) extends IWS {
  def id = bid.toString
  def name = depositor

}
object BankStatement {
  val CENTURY = "20"
  val COMPANY="1000"
  val  COMPANY_IBAN="DE47480501610043006329"
  val zoneId = ZoneId.of( "Europe/Berlin" )
  val DATE_FORMAT = "dd.MM.yyyy"
  val FIELD_SEPARATOR = ';'
  val NUMBER_FORMAT = NumberFormat.getInstance(Locale.GERMAN)


  def   fullDate (partialDate:String):Instant = {
    val index = partialDate.lastIndexOf(".")
    val pYear= partialDate.substring(index+1)
    val fullDate = partialDate.substring(0,index+1).concat(CENTURY.concat(pYear))
    LocalDate.parse(fullDate, DateTimeFormatter.ofPattern(DATE_FORMAT))
      .atStartOfDay(zoneId).toInstant
  }

  def from(s:String ) = {
    val values= s.split(FIELD_SEPARATOR)
    val companyIban = values(0)
    val bid = -1L
    val date1_ = values(1)
    val date2 = values(2)
    val date1 = if (date1_.trim.nonEmpty) date1_ else date2
    val postingdate = fullDate(date1)
    val valuedate  = fullDate(date2)
    //val depositor = values(3)
    val postingtext  = values(3)
    val purpose  = values(4)
    val  beneficiary  = values(5)
    val accountno  = values(6)
    val bankCode = values(7)
    val amount_ = values(8).trim
    val amount  =  BigDecimal(NUMBER_FORMAT.parse(amount_).toString)
    val currency = values(9)
    val  info = values(10)
    val bs = BankStatement(bid, companyIban, postingdate, valuedate,  postingtext, purpose, beneficiary
      , accountno, bankCode, amount, currency, info, COMPANY, companyIban)
    println ("BankStatement>>"+bs)
    bs
  }

}

final case class Company(
  id: String,
  name: String,
  description: String = "",
  street: String,
  city: String,
  state: String,
  zip: String,
  bankAcc: String,
  purchasingClearingAcc: String,
  salesClearingAcc: String,
  paymentClearingAcc: String,
  settlementClearingAcc: String,
  balanceSheetAcc: String,
  incomeStmtAcc: String,
  cashAcc: String,
  taxCode: String,
  vatCode: String,
  currency: String,
  enterdate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  modelid: Int = 10,
  pageHeaderText: String,
  pageFooterText: String,
  headerText: String,
  footerText: String,
  logoContent: String,
  logoName: String,
  contentType: String,
  partner: String,
  phone: String,
  fax: String,
  email: String,
  locale: String //,
  //bankaccounts: List[BankAccount] = Nil
)
