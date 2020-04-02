package com.kabasoft.iws.service

import com.kabasoft.iws.domain._

trait ProcessTransaction[A <: IWS] {
  type PACBalance = PeriodicAccountBalance

  def postAll(transactions: List[A]): List[PACBalance]
  def post(transaction: A): List[PACBalance]

  private[this] def getMapFor(modelId: Int) =
    IWSCache
      .get(modelId)
      .groupBy(_.id)
      .view
      .mapValues(_.head)
      .asInstanceOf[Map[String, PACBalance]]

  protected[this] def postDebit(
    periode: Int,
    accountId: String,
    amount: BigDecimal,
    company: String,
    currency: String,
    modelId: Int
  ) =
    postAccount(periode, accountId, amount, true, company, currency, modelId)
  protected[this] def postCredit(
    periode: Int,
    accountId: String,
    amount: BigDecimal,
    company: String,
    currency: String,
    modelId: Int
  ) =
    postAccount(periode, accountId, amount, false, company, currency, modelId)
  private[this] def postAccount(
    periode: Int,
    accountId: String,
    amount: BigDecimal,
    isDebit: Boolean,
    company: String,
    currency: String,
    modelId: Int
  ) = {
    val pacc = getMapFor(PeriodicAccountBalance.MODELID).getOrElse(
      periode.toString.concat(accountId),
      PeriodicAccountBalance(
        periode.toString.concat(accountId),
        accountId,
        periode,
        BigDecimal(0),
        BigDecimal(0),
        BigDecimal(0),
        BigDecimal(0),
        company,
        currency
      )
    )
    if (isDebit) {
      updateNextBalances(periode, accountId, pacc.debit + amount, isDebit)
      pacc.copy(debit = pacc.debit + amount)

    } else {
      updateNextBalances(periode, accountId, pacc.credit + amount, isDebit)
      pacc.copy(credit = pacc.credit + amount)
    }

  }
  def updatePacc(pacc: PACBalance, amount: BigDecimal, isDebit: Boolean) =
    if (isDebit)
      pacc.copy(idebit = pacc.idebit + amount)
    else
      pacc.copy(icredit = pacc.credit + amount)

  def updateNextBalances(currentPeriode: Int, accountId: String, amount: BigDecimal, isDebit: Boolean): Unit = {
    val l = getMapFor(PeriodicAccountBalance.MODELID)
    for (i <- currentPeriode to 12 - currentPeriode)
      l.get(i.toString.concat(accountId)).toList.map(updatePacc(_, amount, isDebit))
  }

}
object ProcessFinancialsTransaction extends ProcessTransaction[FinancialsTransaction] {
  override def post(transaction: FinancialsTransaction): List[PACBalance] =
    transaction.lines.flatMap(
      line =>
        List(
          postDebit(transaction.periode, line.account, line.amount, line.company, line.currency, transaction.modelId),
          postCredit(transaction.periode, line.oaccount, line.amount, line.company, line.currency, transaction.modelId)
        )
    )
  override def postAll(transactions: List[FinancialsTransaction]): List[PACBalance] = transactions.flatMap(post)

}
