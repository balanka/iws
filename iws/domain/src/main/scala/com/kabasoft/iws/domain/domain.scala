package com.kabasoft.iws.domain

import java.text.SimpleDateFormat

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import cats.implicits._

import common.{Amount, DATE_FORMAT}

import scala.collection.immutable.TreeMap

object common {
  type Amount = scala.math.BigDecimal
  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
}

sealed trait IWS {
  def id: String //MasterfileId
  def modelId: Int
}
sealed trait Masterfile extends IWS {
  def name: String
  def description: String

  def canEqual(a: Any) = a.isInstanceOf[Masterfile]
  override def equals(that: Any): Boolean =
    that match {
      case that: Masterfile =>
        that.canEqual(this) && (this.hashCode == that.hashCode) &&
          this.id.equals(that.id) && (this.modelId == that.modelId)
      case _ => false
    }
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    var r: Int = 0
    def id1(m: String): Int =
      try {
        r = m.toInt
        r
      } catch {
        case _: Throwable =>
          val l: List[Int] = m.toList.map(c => c.toInt)
          r = l.fold(0) { (z, i) =>
            z + i
          }
          r
      } finally {
        // r
      }

    result = prime * result + modelId + id1(id)
    result
  }
}

trait ContainerT[+A <: IWS, -B <: IWS] {
  def update(newItem: B): ContainerT[A, B]
  def updateAll(all: Seq[B]): ContainerT[A, B]
  def remove(item: B): ContainerT[A, B]
  def size = items.size
  def items: Seq[A]
  def add(newItem: B): ContainerT[A, B]
}
final case class MasterfileId(value: String) extends AnyVal

case class Data[A <: IWS](items: Seq[A]) extends ContainerT[A, A] {
  override def update(newItem: A) =
    items.indexWhere((_.id == newItem.id)) match {
      case -1 =>
        Data(items :+ newItem)
      case index =>
        Data(items.updated(index, newItem))
    }
  override def updateAll(all: Seq[A]) = Data((items.toSet ++ all.toSet).toList)
  override def add(newItem: A) = Data(items :+ newItem)
  override def remove(item: A) = Data(items.filterNot(_.id == item.id))
}

object Currency extends Enumeration {
  type ccy = Value
  val CHF, CNY, EUR, DEM, GNF, JPY, USD, XOF = Value
}

final case class Account(
  id: String,
  name: String,
  description: String,
  dateofopen: Date,
  dateofclose: Date,
  balance: BigDecimal,
  company: String,
  parentId: String,
  isDebit: Boolean,
  isBalanceSheetAccount: Boolean,
  posted: Date,
  updated: Date,
  typeJournal: Int,
  modelId: Int,
  isResultAccount: Boolean,
  isIncomeStatementAccount: Boolean,
  subAccounts: List[Account] = List.empty,
  balances: List[PeriodicAccountBalance] = List.empty
) extends Masterfile {
  def add(accounts: List[Account]): Account = copy(subAccounts = accounts)
  def addMe(account: Account): Account = copy(subAccounts = subAccounts :+ account)
}

object Account {
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i: Int) = i == 1

  def addAllSubAccounts(accounts: List[Account]): List[Account] =
    accounts.map(x => x.copy(subAccounts = accounts.filter(_.parentId.equals(x.id))))
  def addBalances(periode: Int, accounts: List[Account], balances: List[PeriodicAccountBalance]): List[Account] =
    accounts.map(x => x.copy(balances = balances.filter(_.periode == periode)))
  def apply(
    id: String,
    name: String,
    description: String,
    dateofopen: String,
    dateofclose: String,
    balance: String,
    company: String,
    parentId: String,
    isDebit: String,
    isBalanceSheetAccount: String,
    posted: String,
    updated: String,
    typeJournal: String,
    modelId: String,
    isResultAccount: String,
    isIncomeStatementAccount: String
  ) =
    new Account(
      id,
      name,
      description,
      dateFormat.parse(dateofopen),
      dateFormat.parse(dateofclose),
      BigDecimal(balance),
      company,
      parentId,
      isDebit.toInt,
      isBalanceSheetAccount.toInt,
      dateFormat.parse(posted),
      dateFormat.parse(updated),
      0,
      modelId.toInt,
      isResultAccount.toInt,
      isIncomeStatementAccount.toInt
    )
}
final case class PeriodicAccountBalance(
  id: String,
  accountId: String,
  periode: Int,
  idebit: BigDecimal,
  icredit: BigDecimal,
  debit: BigDecimal,
  credit: BigDecimal,
  company: String,
  currency: String,
  modelId: Int = PeriodicAccountBalance.MODELID
) extends IWS {
  def fdebit = debit + idebit
  def fcredit = credit + icredit
  def dbalance = fdebit - fcredit
  def cbalance = fcredit - fdebit
}
object PeriodicAccountBalance {
  val MODELID = 106
  def apply(
    accountId: String,
    periode: String,
    idebit: String,
    icredit: String,
    debit: String,
    credit: String,
    company: String,
    currency: String
  ) =
    new PeriodicAccountBalance(
      periode.concat(accountId),
      accountId,
      periode.toInt,
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
}
final case class FinancialsTransaction(
  tid: Long,
  oid: Long,
  costCenter: String,
  account: String,
  transdate: Date,
  enterdate: Date,
  postingdate: Date,
  periode: Int,
  posted: Boolean,
  modelId: Int,
  company: String,
  currency: String,
  text: String,
  typeJournal: Int,
  lines: List[DetailsFinancialsTransaction]
) extends IWS {
  def id = tid.toString
}
object FinancialsTransaction {
  val DATE_FORMAT = "yyyy-MM-dd"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i: Int) = i == 1
  def typeJournal2Int(typeJournal: String) = if (typeJournal.isEmpty) 0 else typeJournal.toInt
  def apply(
    id: String,
    oid: String,
    costCenter: String,
    account: String,
    transdate: String,
    enterdate: String,
    postingdate: String,
    periode: String,
    posted: String,
    modelId: String,
    company: String,
    currency: String,
    text: String,
    typeJournal: String
  ) =
    new FinancialsTransaction(
      id.toLong,
      oid.toLong,
      costCenter,
      account,
      dateFormat.parse(transdate),
      dateFormat.parse(enterdate),
      dateFormat.parse(postingdate),
      periode.toInt,
      posted.toInt,
      modelId.toInt,
      company,
      currency,
      text,
      typeJournal2Int(typeJournal),
      List.empty[DetailsFinancialsTransaction]
    )
}
final case class DetailsFinancialsTransaction(
  lid: Long,
  transId: Long,
  account: String,
  side: Boolean,
  oaccount: String,
  amount: BigDecimal,
  duedate: Date,
  text: String,
  currency: String,
  modelId: Int,
  company: String
) extends IWS {
  def id = lid.toString
  def name = text
}
object DetailsFinancialsTransaction {
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i: Int) = i == 1
  def apply(
    id: String,
    transId: String,
    account: String,
    side: String,
    oaccount: String,
    amount: String,
    duedate: String,
    text: String,
    currency: String,
    modelId: String
  ) =
    new DetailsFinancialsTransaction(
      id.toLong,
      transId.toLong,
      account,
      side.toInt,
      oaccount,
      BigDecimal(amount.replace(".0000", ".00")),
      dateFormat.parse(duedate),
      text,
      currency,
      modelId.toInt,
      "1000"
    )
}
final case class Customer(
  id: String,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zipCode: String,
  tel: String,
  email: String,
  accountId: String,
  companyId: String,
  iban: String,
  vatCode: String,
  oAccountId: String,
  postingdate: Date,
  updated: Date,
  modelId: Int
) extends Masterfile
object Customer {

  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  def apply(
    id: String,
    name: String,
    description: String,
    street: String,
    city: String,
    state: String,
    zipCode: String,
    tel: String,
    email: String,
    accountId: String,
    companyId: String,
    iban: String,
    vatCode: String,
    oAccountId: String,
    postingdate: String,
    updated: String,
    modelId: String
  ) =
    new Customer(
      id,
      name,
      description,
      street,
      city,
      state,
      zipCode,
      tel,
      email,
      accountId,
      companyId,
      iban,
      vatCode,
      oAccountId,
      dateFormat.parse(postingdate),
      dateFormat.parse(updated),
      modelId.toInt
    )
}

final case class Supplier(
  id: String,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zipCode: String,
  tel: String,
  email: String,
  accountId: String,
  companyId: String,
  iban: String,
  vatCode: String,
  oAccountId: String,
  postingdate: Date,
  updated: Date,
  modelId: Int
) extends Masterfile
object Supplier {
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  def apply(
    id: String,
    name: String,
    description: String,
    street: String,
    city: String,
    state: String,
    zipCode: String,
    tel: String,
    email: String,
    accountId: String,
    companyId: String,
    iban: String,
    vatCode: String,
    oAccountId: String,
    postingdate: String,
    updated: String,
    modelId: String
  ) =
    new Supplier(
      id,
      name,
      description,
      street,
      city,
      state,
      zipCode,
      tel,
      email,
      accountId,
      companyId,
      iban,
      vatCode,
      oAccountId,
      dateFormat.parse(postingdate),
      dateFormat.parse(updated),
      modelId.toInt
    )
}
final case class BankAccount(iban: String, owner: String, bic: String, companyId: String, modelId: Int = 12)
    extends IWS {
  def id: String = iban
  def name: String = owner

}
object BankAccount {
  def apply(iban: String, owner: String, bic: String, companyId: String) =
    new BankAccount(iban, owner, bic, companyId)
}
final case class BankStatement(
  auftragskonto: String,
  buchungstag: String,
  valutadatum: String,
  buchungstext: String,
  verwendungszweck: String,
  beguenstigter: String,
  kontonummer: String,
  blz: String,
  betrag: String,
  waehrung: String,
  info: String,
  modelId: Int = 999
) extends IWS {
  def id = auftragskonto
  def name = buchungstext
}

trait Cache[A <: IWS] {

  def +(item: A): Option[A] = update(item)
  def update(item: A): Option[A]
  def updateAll(l: List[A]): Option[Data[A]]
  def get(item: A): Seq[A]
  def get(modelId: Int): Seq[A]
  def get(id: String, modelId: Int): Option[A]
  def delete(item: A): Data[A]
  def list(item: A, pageSize: Int, offset: Int): List[A]
  def all(modelId: Int, pageSize: Int, offset: Int): List[A]

}
object MasterfileCache extends Cache[Masterfile] { //extends Subject [IWS, IWS]  with  Observer [IWS]{

  private var cache = new TreeMap[Int, Data[Masterfile]]
  override def update(item: Masterfile): Option[Masterfile] = {

    if (!cache.contains(item.modelId)) {
      cache += (item.modelId -> Data(List(item)))
    } else {
      cache.getOrElse(item.modelId, Data(List(item))).update(item)
    }
    Some(item)

  }

  override def updateAll(l: List[Masterfile]): Option[Data[Masterfile]] =
    if (l == null || l.isEmpty) return None
    else {
      val l2: Data[Masterfile] = cache.getOrElse(l.head.modelId, Data(l))
      cache += (l.head.modelId -> l2.updateAll(l))
      Some(l2)
    }

  override def get(item: Masterfile): Seq[Masterfile] =
    cache.getOrElse(item.modelId, Data(List.empty[Masterfile])).items
  override def get(modelId: Int): Seq[Masterfile] = cache.getOrElse(modelId, Data(List.empty[Masterfile])).items
  override def get(id: String, modelId: Int): Option[Masterfile] =
    cache.getOrElse(modelId, Data(List.empty[Masterfile])).items.filter(_.id.equals(id)).headOption
  override def delete(item: Masterfile): Data[Masterfile] =
    cache.getOrElse(item.modelId, Data(List.empty[Masterfile])).remove(item)
  override def list(item: Masterfile, pageSize: Int, offset: Int): List[Masterfile] =
    cache
      .getOrElse(item.modelId, Data(List.empty[Masterfile]))
      .items
      .toList
      .sortBy(_.id)
      .slice(offset, offset + pageSize)

  override def all(modelId: Int, pageSize: Int, offset: Int): List[Masterfile] =
    cache.getOrElse(modelId, Data(List.empty[Masterfile])).items.toList.sortBy(_.id).slice(offset, offset + pageSize)
}

object IWSCache extends Cache[IWS] {
//def make (c:Map[Int, Data[IWS]]) {}
  private var cache = new TreeMap[Int, Data[IWS]]
  override def update(item: IWS): Option[IWS] = {
    cache.getOrElse(item.modelId, Data(List(item))).update(item)
    Some(item)

  }

  def updateAll(l: List[IWS]): Option[Data[IWS]] =
    if (l == null || l.isEmpty) return None
    else {
      val l2: Data[IWS] = cache.getOrElse(l.head.modelId, Data(l))
      cache.getOrElse(l.head.modelId, l2.updateAll(l)).updateAll(l2.items) //.update(l2)
      //cache +=(l.head.modelId -> l2.updateAll(l))
      Some(l2)
    }

  def get(item: IWS): Seq[IWS] = cache.getOrElse(item.modelId, Data(List.empty[IWS])).items
  def get(modelId: Int): Seq[IWS] = cache.getOrElse(modelId, Data(List.empty[IWS])).items
  def get(id: String, modelId: Int): Option[IWS] =
    cache.getOrElse(modelId, Data(List.empty[IWS])).items.filter(_.id.equals(id)).headOption
  def delete(item: IWS): Data[IWS] = cache.getOrElse(item.modelId, Data(List.empty[IWS])).remove(item)
  def list(item: IWS, pageSize: Int, offset: Int): List[IWS] = all(item.modelId, pageSize, offset)
  def all(modelId: Int, pageSize: Int, offset: Int): List[IWS] =
    cache.getOrElse(modelId, Data(List.empty[IWS])).items.toList.sortBy(_.id).slice(offset, offset + pageSize)
}

object FinancialsTransactionCache extends Cache[FinancialsTransaction] {

  private var cache = new TreeMap[Int, Data[FinancialsTransaction]]
  override def update(item: FinancialsTransaction): Option[FinancialsTransaction] = {

    if (!cache.contains(item.modelId)) {
      cache += (item.modelId -> Data(List(item)))
    } else {
      cache.getOrElse(item.modelId, Data(List(item))).update(item)
    }
    Some(item)

  }

  override def updateAll(l: List[FinancialsTransaction]): Option[Data[FinancialsTransaction]] =
    if (l == null || l.isEmpty) return None
    else {
      val l2: Data[FinancialsTransaction] = cache.getOrElse(l.head.modelId, Data(l))
      cache += (l.head.modelId -> l2.updateAll(l))
      Some(l2)
    }

  override def get(item: FinancialsTransaction): Seq[FinancialsTransaction] =
    cache.getOrElse(item.modelId, Data(List.empty[FinancialsTransaction])).items
  override def get(modelId: Int): Seq[FinancialsTransaction] =
    cache.getOrElse(modelId, Data(List.empty[FinancialsTransaction])).items
  override def get(id: String, modelId: Int): Option[FinancialsTransaction] =
    cache.getOrElse(modelId, Data(List.empty[FinancialsTransaction])).items.filter(_.id.equals(id)).headOption
  override def delete(item: FinancialsTransaction): Data[FinancialsTransaction] =
    cache.getOrElse(item.modelId, Data(List.empty[FinancialsTransaction])).remove(item)
  override def list(item: FinancialsTransaction, pageSize: Int, offset: Int): List[FinancialsTransaction] =
    cache
      .getOrElse(item.modelId, Data(List.empty[FinancialsTransaction]))
      .items
      .toList
      .sortBy(_.id)
      .slice(offset, offset + pageSize)

  override def all(modelId: Int, pageSize: Int, offset: Int): List[FinancialsTransaction] =
    cache
      .getOrElse(modelId, Data(List.empty[FinancialsTransaction]))
      .items
      .toList
      .sortBy(_.id)
      .slice(offset, offset + pageSize)
}

sealed abstract case class BankAccount2 private (
  ownerId: String,
  balance: BigDecimal,
  accountType: String,
  openedDate: Long
)
object BankAccount2 {
  sealed trait BankAccountError
  case object IllegalOwnerId extends BankAccountError
  case object IllegalBalance extends BankAccountError
  case object IllegalAccountType extends BankAccountError
  case object IllegalOpenedDate extends BankAccountError

  def from(
    ownerId: String,
    balance: BigDecimal,
    accountType: String,
    openedDate: Long
  ): Either[BankAccountError, BankAccount2] =
    for {
      validOwnerId <- Either.cond(
        ownerId.length() == 8 && scala.util.Try(ownerId.toInt).isSuccess,
        ownerId,
        IllegalOwnerId
      )
      validBalance <- Either.cond(
        balance >= BigDecimal(0),
        balance,
        IllegalBalance
      )
      validAccountType <- Either.cond(
        Set("type1", "type2").contains(accountType),
        accountType,
        IllegalAccountType
      )
      validOpenedDate <- Either.cond(
        System.nanoTime() >= openedDate && openedDate >= 0L,
        openedDate,
        IllegalOpenedDate
      )
    } yield new BankAccount2(validOwnerId, validBalance, validAccountType, validOpenedDate) {}
}

final case class Routes(id: String, name: String, description: String, modelId: Int, component: String)
    extends Masterfile
