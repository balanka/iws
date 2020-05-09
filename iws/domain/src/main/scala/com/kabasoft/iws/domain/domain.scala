package com.kabasoft.iws.domain

import java.time.Instant
import java.time.temporal.ChronoField

import com.kabasoft.iws.domain.FinancialsTransaction.FinancialsTransaction_Type2
import com.kabasoft.iws.domain.commom.Amount

object commom {
  type Amount = scala.math.BigDecimal
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
  balancesheet: Boolean
) extends IWS
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
  period: Int = Instant.now().get(ChronoField.YEAR),
  posted: Boolean = false,
  modelid: Int,
  company: String,
  text: String = "",
  typeJournal: Int = 0,
  file_content: Int = 0,
  lines: List[FinancialsTransactionDetails] = Nil
) extends IWS {
  def id = tid.toString
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
