package com.toracoya.petstore.pet

import java.time.Instant
import java.time.temporal.ChronoField

import com.toracoya.petstore.pet.commom.Amount

object commom {
  type Amount = scala.math.BigDecimal
}
sealed trait IWS {
  def id: MasterfileId
}
object Currency extends Enumeration {
  type ccy = Value
  val CHF, CNY, EUR, DEM, GNF, JPY, USD, XOF = Value
}
case class Pet(id: MasterfileId, name: String)
final case class Masterfile(id: MasterfileId, name: String, description: String, modelId: Int, parent: String)
    extends IWS
final case class Routes(id: MasterfileId, name: String, description: String, modelId: Int, component: String)
    extends IWS
final case class Article(
  id: MasterfileId,
  name: String = "",
  description: String = "",
  modelId: Int,
  parent: String = "",
  price: Amount = 0.0,
  stocked: Boolean = false
) extends IWS
final case class Account(
  id: MasterfileId,
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
  id: MasterfileId,
  account: String,
  periode: Int,
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
    periode: String,
    idebit: String,
    icredit: String,
    debit: String,
    credit: String,
    company: String,
    currency: String
  ) =
    new PeriodicAccountBalance(
      MasterfileId(periode.concat(account)),
      account,
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
final case class CostCenter(
  id: MasterfileId,
  name: String = "",
  description: String = "",
  account: String = "",
  modelId: Int = 6,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now()
) extends IWS
final case class Supplier(
  id: MasterfileId,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zip: String,
  phone: String,
  email: String,
  account: String,
  oaccount: String,
  iban: String,
  vatcode: String,
  company: String,
  modelId: Int = 3,
  enterdate: Instant = Instant.now(),
  changedate: Instant = Instant.now(),
  postingdate: Instant = Instant.now()
) extends IWS
final case class Customer(
  id: MasterfileId,
  name: String,
  description: String,
  street: String,
  city: String,
  state: String,
  zip: String,
  phone: String,
  email: String,
  account: String,
  oaccount: String,
  iban: String,
  vatcode: String,
  company: String,
  modelId: Int = 3,
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
  def id = MasterfileId(lid.toString)
}
object FinancialsTransactionDetails {
  import com.toracoya.petstore.pet.FinancialsTransaction.FinancialsTransaction_Type2
  type FinancialsTransactionDetails_Type =
    (Long, Long, String, Boolean, String, String, Instant, String, String, String)
  def apply(tr: FinancialsTransactionDetails_Type): FinancialsTransactionDetails =
    new FinancialsTransactionDetails(
      tr._1,
      tr._2,
      tr._3,
      tr._4,
      tr._5,
      BigDecimal(tr._6.replace(",", "").replace("4", "")),
      tr._7,
      tr._8,
      tr._9,
      tr._10
    )
  def apply(tr: FinancialsTransaction_Type2): FinancialsTransactionDetails = {
    println("x19========> " + tr._19)
    println("x19R=======XX " + tr._19.replace(",", "").replace("$", ""))
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
}
final case class FinancialsTransaction(
  tid: Long,
  oid: Long,
  costcenter: String,
  account: String,
  transdate: Instant = Instant.now(),
  enterdate: Instant = Instant.now(),
  postingdate: Instant = Instant.now(),
  periode: Int = Instant.now().get(ChronoField.YEAR),
  posted: Boolean = false,
  modelId: Int,
  company: String,
  text: String = "",
  typeJournal: Int = 0,
  file_content: Int = 0,
  lines: List[FinancialsTransactionDetails] = Nil
) extends IWS {
  def id = MasterfileId(tid.toString)
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
          ).copy(lines = v.map(FinancialsTransactionDetails.apply))
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
  periode: Int,
  amount: BigDecimal,
  side: Boolean,
  company: String,
  currency: String,
  text: String = "",
  month: Int,
  year: Int,
  modelId: Int,
  idebit: BigDecimal,
  icredit: BigDecimal,
  typeJournal: Int = 0,
  file_content: Int = 0,
  debit: BigDecimal,
  credit: BigDecimal
)
object Journal {
  //type Journal_Type = (Long, Long, Long, String, String, Instant, Instant, Instant,
  //  Int, BigDecimal, Boolean, String, String, String, Int, Int, Int,BigDecimal,
  //  BigDecimal, Int, Int, BigDecimal, BigDecimal )

  def apply(x: Journal) = {
    println("transdate ::: " + x.transdate);
    println("transdate ::: " + x.postingdate);
    println("transdate ::: " + x.enterdate);
    new Journal(
      x.id,
      x.transid,
      x.oid,
      x.account,
      x.oaccount,
      x.transdate,
      x.postingdate,
      x.enterdate,
      x.periode,
      x.amount,
      x.side,
      x.company,
      x.currency,
      x.text,
      x.month,
      x.year,
      x.modelId,
      x.idebit,
      x.icredit,
      x.typeJournal,
      x.file_content,
      x.debit,
      x.credit
    )
  }
}
