package com.kabasoft.iws.repository.doobie

import cats.data.NonEmptyList
import cats.implicits._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0
import com.kabasoft.iws.domain.Account.Account_Type
import com.kabasoft.iws.domain._
import com.kabasoft.iws.domain.FinancialsTransaction.{FinancialsTransaction_Type, FinancialsTransaction_Type2}
import doobie.Fragments

trait Repository[-A, B] {
  def create(item: A): Update0
  def create(models: List[A]): List[Update0] = models.map(create)
  def delete(item: String, company: String): Update0
  def delete(items: List[String], company: String): List[Update0]= items.map(delete(_, company))
  def list(company: String): Query0[B]
  def getBy(id: String, company: String): Query0[B]
  def getByModelId(modelid: Int, company: String): Query0[B]
  def update(models: List[A], company: String): List[Update0] = models.map(update(_, company))
  def update(model: A, company: String): Update0
  def findSome(company: String, param: String*): Query0[B]
}
private object SQL {

  object Article extends Repository[Article, Article] {
    def create(item: Article): Update0 =
      sql"""INSERT INTO article (ID, NAME, DESCRIPTION, MODELID, PARENT, PRICE, STOCKED, COMPANY) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid}, ${item.parent},  ${item.price},  ${item.stocked},  ${item.company})""".update

    def getBy(id: String, company: String): Query0[Article] = sql"""
     SELECT id, name, description, modelId, price, parent, stocked
     FROM article
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
    """.query

    def getByModelId(modelid: Int, company: String): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article
     WHERE modelid = $modelid AND COMPANY=${company} ORDER BY  id ASC
    """.query
    def findSome(company: String, model: String*): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article WHERE  COMPANY=${company}
     ORDER BY  id ASC """.query

    def list(company: String): Query0[Article] = sql"""
     SELECT id, name, description, modelid, parent,  price, stocked
     FROM article WHERE  COMPANY=${company}
     ORDER BY id ASC
     """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM article WHERE ID = $id AND COMPANY=${company}""".update

    def update(model: Article, company: String): Update0 = sql"""
         Update article set id = ${model.id}, name =${model.name} description =${model.description}
         parent =${model.parent},  price =${model.price} , stocked=${model.stocked}  where id =${model.id}
         AND COMPANY=${company} """.update
  }
  object Bank extends Repository[Bank, Bank] {

    def create(item: Bank): Update0 =
      sql"""INSERT INTO bank (ID, NAME, DESCRIPTION, modelid, COMPANY) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.company} )""".update

    def getBy(id: String, company: String): Query0[Bank] = sql"""
     SELECT id, name, description, enter_date, modified_date, posting_date,modelid, company
     FROM Bank
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Bank] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, company
        FROM Bank  WHERE modelid = $modelid ORDER BY  id ASC
         """.query
    def findSome(company: String, model: String*): Query0[Bank] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, company as component
        FROM Bank   WHERE COMPANY=${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[Bank] = sql"""
     SELECT id, name,description, enter_date, modified_date, posting_date, modelid, company
     FROM Bank WHERE COMPANY=${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM Bank WHERE ID = $id AND COMPANY=${company}""".update

    def update(model: Bank, company: String): Update0 = sql"""Update Bank set id = ${model.id}, name =${model.name},
         description=${model.description}  where id =${model.id} AND COMPANY=${company} """.update
  }
  object Module extends Repository[Module, Module] {

    def create(item: Module): Update0 =
      sql"""INSERT INTO module (ID, NAME, DESCRIPTION, modelid, COMPANY) VALUES
     (${item.id}, ${item.name}, ${item.description}, ${item.modelid},  ${item.company} )""".update

    def getBy(id: String, company: String): Query0[Module] = sql"""
     SELECT id, name, description, enter_date, modified_date, posting_date,modelid, company
     FROM module
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Module] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, company
        FROM module  WHERE modelid = $modelid ORDER BY  id ASC
         """.query
    def findSome(company: String, model: String*): Query0[Module] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, company as component
        FROM module   WHERE COMPANY=${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[Module] = sql"""
     SELECT id, name,description, enter_date, modified_date, posting_date, modelid, company
     FROM module WHERE COMPANY=${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM module WHERE ID = $id AND COMPANY=${company}""".update

    def update(model: Module, company: String): Update0 = sql"""Update module set id = ${model.id}, name =${model.name},
         description=${model.description}  where id =${model.id} AND COMPANY=${company} """.update
  }
  object Routes extends Repository[Routes, Routes] {

    def create(item: Routes): Update0 =
      sql"""INSERT INTO Routes (ID, NAME, DESCRIPTION, MODELID, PARENT) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.component} )""".update

    def getBy(id: String, company: String): Query0[Routes] = sql"""
     SELECT id, name, description, modelid, parent as component
     FROM Routes
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Routes] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes  WHERE modelid = $modelid AND COMPANY=${company} ORDER BY  id ASC
         """.query
    def findSome(company: String, model: String*): Query0[Routes] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes   WHERE COMPANY=${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[Routes] = sql"""
     SELECT id, name,description, modelid, parent as component
     FROM Routes WHERE COMPANY=${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM Routes WHERE ID = $id AND COMPANY=${company} """.update

    def update(model: Routes, company: String): Update0 = sql"""Update Routes set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.component}  where id =${model.id} AND COMPANY=${company}""".update
  }
  object Company extends Repository[Company, Company] {
    def create(item: Company): Update0 =
      sql"""INSERT INTO company (ID, NAME, DESCRIPTION, modelid) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid} )""".update

    def getBy(id: String, company: String): Query0[Company] = sql"""
    select id, name, description, street, city, state, zip, bankAcc, purchasingClearingAcc, salesClearingAcc
     , paymentClearingAcc,settlementClearingAcc, balanceSheetAcc, incomeStmtAcc, cashAcc
     ,  taxCode, vatCode, currency, enterdate, postingdate,changedate, modelid, pageHeaderText, pageFooterText
     , headerText,footerText,logoContent, logoName, contentType, partner, tel, fax, email,locale
     FROM company WHERE id = $id """.query

    def getByModelId(modelid: Int, company: String): Query0[Company] = sql"""
      select id, name, description, street, city, state, zip, bankAcc, purchasingClearingAcc, salesClearingAcc
     , paymentClearingAcc,settlementClearingAcc, balanceSheetAcc, incomeStmtAcc, cashAcc
     ,  taxCode, vatCode, currency, enterdate, postingdate,changedate, modelid, pageHeaderText, pageFooterText
     , headerText,footerText,logoContent, logoName, contentType, partner, tel, fax, email,locale
      FROM company
   WHERE modelid = $modelid AND ID=${company}  """.query

    def findSome(company: String, model: String*): Query0[Company] = sql"""
     select id, name, description, street, city, state, zip, bankAcc, purchasingClearingAcc, salesClearingAcc
     , paymentClearingAcc,settlementClearingAcc, balanceSheetAcc, incomeStmtAcc, cashAcc
     ,  taxCode, vatCode, currency, enterdate, postingdate,changedate, modelid, pageHeaderText, pageFooterText
     , headerText,footerText,logoContent, logoName, contentType, partner, tel, fax, email,locale
    FROM company FROM company C
    WHERE C.id=${company} ORDER BY  C.id ASC""".query

    def list(company: String): Query0[Company] = sql"""
      select id, name, description, street, city, state, zip, bankAcc, purchasingClearingAcc, salesClearingAcc
     , paymentClearingAcc,settlementClearingAcc, balanceSheetAcc, incomeStmtAcc, cashAcc
     ,  taxCode, vatCode, currency, enterdate, postingdate,changedate, modelid, pageHeaderText, pageFooterText
     , headerText,footerText,logoContent, logoName, contentType, partner, tel, fax, email,locale
   FROM company C
    WHERE  ID=${company} ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM company C WHERE C.ID = $id""".update

    def update(model: Company, company: String): Update0 =
      sql"""Update company C set C.id = ${model.id}, C.name =${model.name}, C.description=${model.description}
      street =${model.street}, city=${model.city}, state =${model.state}, zip=${model.zip}, bankacc=${model.bankAcc}
     , purchasingclearingacc=${model.purchasingClearingAcc}, salesclearingacc=${model.salesClearingAcc}
     , paymentclearingacc =${model.paymentClearingAcc}, settlementclearingacc=${model.settlementClearingAcc}
     , balanceSheetAcc =${model.balanceSheetAcc}, incomeStmtAcc=${model.incomeStmtAcc}
     , cashAccount=${model.cashAcc}, taxcode=${model.taxCode}, vatcode=${model.vatCode}, locale =${model.locale}
     , tel=${model.tel}, fax=${model.fax}, partner=${model.partner}, email=${model.email}
           where C.id =${model.id}""".update
  }
  object Masterfile extends Repository[Masterfile, Masterfile] {
    def create(item: Masterfile): Update0 =
      sql"""INSERT INTO masterfiles (ID, NAME, DESCRIPTION, modelid, PARENT) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.parent} )""".update

    def getBy(id: String, company: String): Query0[Masterfile] = sql"""
     SELECT id, name, description, modelid, parent
     FROM masterfiles
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles  WHERE modelid = $modelid AND COMPANY=${company} ORDER BY  id ASC
         """.query

    def findSome(company: String, model: String*): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles   WHERE COMPANY=${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[Masterfile] = sql"""
     SELECT id, name,description, modelid, parent FROM masterfiles WHERE AND COMPANY=${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM masterfiles WHERE ID = $id AND COMPANY=${company}""".update

    def update(model: Masterfile, company: String): Update0 =
      sql"""Update masterfiles set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.parent}  where id =${model.id} AND COMPANY=${company}""".update
  }
  object CostCenter extends Repository[CostCenter, CostCenter] {

    def create(item: CostCenter): Update0 =
      sql"""INSERT INTO costcenter (ID, NAME, DESCRIPTION, modelid, ACCOUNT, COMPANY) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.account}, ${item.company} )""".update

    def getBy(id: String, company: String): Query0[CostCenter] = sql"""
     SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
     FROM costcenter
     WHERE id = $id AND COMPANY=${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[CostCenter] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
        FROM costcenter  WHERE modelid = $modelid AND COMPANY=${company} ORDER BY  id ASC
         """.query

    def findSome(company: String, model: String*): Query0[CostCenter] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
        FROM costcenter   WHERE  COMPANY=${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[CostCenter] = sql"""
     SELECT id, name,description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
     FROM costcenter WHERE COMPANY=${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM costcenter WHERE ID = $id AND COMPANY=${company} """.update

    def update(model: CostCenter, company: String): Update0 =
      sql"""Update costcenter set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}, company=${model.company}
            where id =${model.id} AND COMPANY=${company}""".update
  }
  object Account extends Repository[Account, Account_Type] {
    def create(item: Account): Update0 =
      sql"""INSERT INTO account (ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company
             , modelid, ACCOUNT, isDebit, balancesheet) VALUES  (${item.id}, ${item.name}, ${item.description}
             , ${item.postingdate}, ${item.changedate}, ${item.enterdate}, ${item.company}, ${item.modelid}
             , ${item.account}, ${item.isDebit}, ${item.balancesheet})""".update

    def getBy(id: String, company: String): Query0[Account_Type] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet , currency, IDEBIT, ICREDIT, DEBIT, CREDIT
     FROM account
     WHERE id = $id AND COMPANY = ${company}  ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Account_Type] = sql"""
        SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet , currency, IDEBIT, ICREDIT, DEBIT, CREDIT
        FROM account WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC
         """.query

    def findSome(company: String, model: String*): Query0[Account_Type] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet , currency, IDEBIT, ICREDIT, DEBIT, CREDIT
            FROM account WHERE COMPANY = ${company} ORDER BY id ASC """.query

    def list(company: String): Query0[Account_Type] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet , currency, IDEBIT, ICREDIT, DEBIT, CREDIT
            FROM account WHERE COMPANY = ${company} ORDER BY id ASC """.query

    def listX(fromPeriod: Int, toPeriod: Int, company: String): Query0[Account_Type] =
      sql"""SELECT A.ID, A.NAME, A.DESCRIPTION, A.posting_date
                   , A.modified_date, A.enter_date, A.company, A.modelid, A.ACCOUNT,  A.isDebit, A.balancesheet
                  , CASE WHEN P.currency is NULL THEN '' ELSE P.currency END AS currency
                  , CASE WHEN P.IDEBIT is NULL THEN 0 ELSE P.IDEBIT END AS IDEBIT
                  , CASE WHEN P.ICREDIT is NULL THEN 0 ELSE P.ICREDIT END AS ICREDIT
                  , CASE WHEN P.DEBIT is NULL THEN 0 ELSE P.DEBIT END AS DEBIT
                  , CASE WHEN P.CREDIT is NULL THEN 0 ELSE P.CREDIT END AS CREDIT
                  FROM account A LEFT JOIN (select account, 0 as IDebit, SUM(DEBIT) as DEBIT, 0 as ICredit, SUM(CREDIT) as CREDIT, max(period) as Period, currency
               from periodic_account_balance Where  period between ${fromPeriod} and ${toPeriod} AND COMPANY= ${company} group by account, currency) P ON A.id = P.account
                  AND   A.COMPANY= ${company} AND P.period between ${fromPeriod} and ${toPeriod}
                  ORDER BY A.id""".query[Account_Type]

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM account WHERE ID = $id AND COMPANY= ${company} """.update

    def update(model: Account, company: String): Update0 = {
      println("model: " + model)
      sql"""Update account set  NAME =${model.name}, DESCRIPTION=${model.description}
        , posting_date =${model.postingdate}, modified_date =${model.changedate}, enter_date = ${model.enterdate}
        , company =${model.company}, account =${model.account}, isDebit =${model.isDebit}, balancesheet =${model.balancesheet}
         where id =${model.id} AND COMPANY = ${company}""".update
    }

  }

  object PeriodicAccountBalance extends Repository[PeriodicAccountBalance, PeriodicAccountBalance] {
    def create(item: PeriodicAccountBalance): Update0 = {
      println("item>>>>" + item)
      sql"""INSERT INTO periodic_account_balance (ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid) VALUES (
              ${item.id}, ${item.account}, ${item.period}, ${item.idebit}, ${item.icredit}, ${item.debit}, ${item.credit}
             , ${item.company}, ${item.currency}, ${item.modelid} )""".update
    }

    def getBy(id: String, company: String): Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
     FROM periodic_account_balance
     WHERE id = $id AND COMPANY = ${company} ORDER BY  PERIOD ASC
     """.query[PeriodicAccountBalance]

    def getBy(ids: List[String], company: String): List[Query0[PeriodicAccountBalance]] = ids.map(getBy(_, company))

    def getByModelId(modelid: Int, company: String): Query0[PeriodicAccountBalance] = sql"""
        SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  PERIOD ASC
         """.query

    def findSome(company: String, model: String*): Query0[PeriodicAccountBalance] = {
      val acc: String = model(0)
      val fromPeriod: Int = model(1).toInt
      val toPeriod: Int = model(2).toInt

      sql"""SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE ACCOUNT = ${acc} AND COMPANY = ${company} AND PERIOD BETWEEN ${fromPeriod} AND ${toPeriod}
        ORDER BY  ID ASC """.query
    }

    def find4Period(company: String, model: Seq[Int]): Query0[PeriodicAccountBalance] = {
      val fromPeriod: Int = model(0)
      val toPeriod: Int = model(1)
      sql"""SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE  COMPANY = ${company} AND PERIOD BETWEEN ${fromPeriod} AND ${toPeriod}
        ORDER BY  ID ASC """.query
    }

    def findBalance4Period(company: String, model: Seq[Int]): Query0[PeriodicAccountBalance] = {
      val fromPeriod: Int = model(0)
      val toPeriod: Int = model(1)
      sql"""SELECT '0' as ID, ACCOUNT, 0 as PERIOD, 0 as IDEBIT , 0 AS ICREDIT, SUM(DEBIT) AS DEBIT
             , SUM(CREDIT)  AS CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE COMPANY = ${company} AND PERIOD BETWEEN ${fromPeriod} AND ${toPeriod}
        GROUP BY  ACCOUNT, company, CURRENCY, modelid ORDER BY ACCOUNT ASC """.query
    }
    def list(company: String): Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
     FROM periodic_account_balance WHERE COMPANY = ${company}
     ORDER BY PERIOD ASC """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM periodic_account_balance WHERE ID = $id AND COMPANY = ${company}""".update

    def update(model: PeriodicAccountBalance, company: String): Update0 = sql"""Update periodic_account_balance
         set IDEBIT=${model.idebit}, ICREDIT=${model.icredit}, DEBIT=${model.debit}, CREDIT=${model.credit}
         , COMPANY=${model.company}, CURRENCY =${model.currency} where id =${model.id}  AND COMPANY = ${company}""".update

    def findSome(model: PeriodicAccountBalance, company: String) = sql"""
      SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
      FROM periodic_account_balance
      WHERE ACCOUNT=${model.account} AND COMPANY = ${company} and PERIOD BETWEEN ${model.period} and ${model.period}
      ORDER BY PERIOD ASC """.query
  }
  object Customer extends Repository[Customer, Customer] {

    def create(item: Customer): Update0 =
      sql"""INSERT INTO customer (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY,
            PHONE, EMAIL, ACCOUNT, REVENUE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES
     (${item.id}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
     , ${item.state},  ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
     , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def getBy(id: String, company: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY,modelid,  enter_date, modified_date, posting_date
     FROM customer WHERE id = $id AND COMPANY = ${company} ORDER BY  id ASC
     """.query

    def getByAccount(accountId: String, company: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY,modelid,  enter_date, modified_date, posting_date
     FROM customer WHERE ACCOUNT = $accountId AND COMPANY = ${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer  WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC
         """.query

    def findSome(company: String, model: String*): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer   WHERE  COMPANY = ${company} ORDER BY  id ASC
         """.query

    def getByIBAN(iban: String, company: String): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, REVENUE_ACCOUNT
        , C.IBAN, VATCODE, C.COMPANY, C.modelid,  enter_date, modified_date, posting_date
        FROM customer C, BankAccount B WHERE B.iban =${iban} AND C.id = B.owner AND C.COMPANY = ${company} ORDER BY  C.id ASC
         """.query

    def list(company: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid, enter_date, modified_date, posting_date
     FROM customer WHERE  COMPANY = ${company}
     ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM customer WHERE ID = $id AND COMPANY = ${company}""".update

    def update(model: Customer, company: String): Update0 = {
      val sq = sql"""Update customer set  name =${model.name}, description=${model.description}
         , street=${model.street}, city=${model.city}, state = ${model.state}, zip=${model.zip}, country=${model.country}
         , phone= ${model.phone},  email= ${model.email}, account=${model.account},  revenue_account=${model.oaccount}
         , iban =${model.iban}, vatcode=${model.vatcode},  company=${model.company}
         where id =${model.id} AND COMPANY = ${company}""".update
      println("sql>>>>" + sq.sql)
      sq
    }
  }
  object Supplier extends Repository[Supplier, Supplier] {

    def create(item: Supplier): Update0 =
      sql"""INSERT INTO supplier (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY
            PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES(
            ${item.id}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
           , ${item.state}, ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
           , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def getBy(id: String, company: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier
     WHERE id = $id AND COMPANY = ${company} ORDER BY  id ASC
     """.query

    def getByAccount(accountId: String, company: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier
     WHERE ACCOUNT = $accountId AND COMPANY = ${company} ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int, company: String): Query0[Supplier] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
        FROM supplier  WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC
         """.query

    def getByIBAN(iban: String, company: String): Query0[Supplier] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,CHARGE_ACCOUNT
             , S.IBAN, VATCODE, S.COMPANY, S.modelid,  enter_date, modified_date, posting_date
        FROM supplier S, BankAccount B WHERE B.iban =${iban} AND S.id = B.owner AND S.COMPANY = ${company} ORDER BY  S.id ASC
         """.query

    def list(company: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
     , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier WHERE COMPANY = ${company} ORDER BY id  ASC """.query

    def findSome(company: String, model: String*): Query0[Supplier] = sql"""
      SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
    , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
    FROM supplier WHERE  COMPANY = ${company} ORDER BY id  ASC """.query
    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM dual WHERE ID = $id AND COMPANY = ${company}""".update

    def update(model: Supplier, company: String): Update0 =
      sql"""Update supplier set name =${model.name}, description=${model.description}
         , street=${model.street}, city=${model.city},state = ${model.state}, zip=${model.zip}, country=${model.country}
         , phone= ${model.phone}, email= ${model.email}, account=${model.account}, charge_account=${model.oaccount}
         , iban =${model.iban}, vatcode=${model.vatcode}, company=${model.company}
         where id =${model.id} AND COMPANY = ${company}""".update

  }
  object FinancialsTransactionDetails extends Repository[FinancialsTransactionDetails, FinancialsTransactionDetails] {

    def create(model: FinancialsTransactionDetails): Update0 =
      sql"""INSERT INTO details_compta (ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT,DUEDATE, TEXT, CURRENCY, COMPANY) VALUES
     (nextval('details_compta_id_seq'), ${model.transid}, ${model.account}, ${model.side}, ${model.oaccount}, ${model.amount}
    , ${model.duedate}, ${model.text},  ${model.currency}, ${model.company} )""".update

    def getBy(id: String, company: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE id = $id AND COMPANY = ${company} ORDER BY  id ASC """.query

    def getByTransId(transid:Long, company: String): Query0[FinancialsTransactionDetails] =
     sql"""SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE transid =${transid} AND COMPANY = ${company} """.query


    def findSome(company: String, model: String*): Query0[FinancialsTransactionDetails] = {
      val transid = model(0).toLong
      sql"""SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta
     WHERE TRANSID =${transid} AND COMPANY = ${company} ORDER BY  id ASC """.query[FinancialsTransactionDetails]
    }

    def getByModelId(modelid: Int, company: String): Query0[FinancialsTransactionDetails] = sql"""
        SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
        FROM details_compta  WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC
         """.query

    def list(company: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE COMPANY = ${company} ORDER BY id  ASC
  """.query

    def delete(idx: String, company: String): Update0 =
      sql"""DELETE FROM details_compta WHERE ID = ${idx.toLong} AND COMPANY = ${company} """.update

    def SQL_UPDATE(model: FinancialsTransactionDetails, company: String) = sql"""Update details_compta
     set TRANSID=${model.transid}, ACCOUNT=${model.account}, SIDE=${model.side}, OACCOUNT=${model.oaccount},
     AMOUNT=${model.amount}, DUEDATE=${model.duedate}, TEXT=${model.text},  CURRENCY=${model.currency}, COMPANY=${model.company}
     where id =${model.lid} AND COMPANY = ${company}"""

    override def update(models: List[FinancialsTransactionDetails], company: String): List[Update0] =
      models.map(update(_, company))

    def update(model: FinancialsTransactionDetails, company: String): Update0 = sql"""Update details_compta
     set ACCOUNT=${model.account}, SIDE=${model.side}, OACCOUNT=${model.oaccount},
     AMOUNT=${model.amount}, DUEDATE=${model.duedate}, TEXT=${model.text},  CURRENCY=${model.currency}
     where id =${model.lid} AND COMPANY = ${company}""".update
  }
  object FinancialsTransaction extends Repository[FinancialsTransaction, FinancialsTransaction_Type2] {

    def createDetails(model: FinancialsTransactionDetails, transid: Long) =
      sql"""INSERT INTO details_compta (ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT,DUEDATE, TEXT, CURRENCY, COMPANY) VALUES
       (nextval('details_compta_id_seq'), ${transid}, ${model.account}, ${model.side}, ${model.oaccount}, ${model.amount}
      , ${model.duedate}, ${model.text},  ${model.currency}, ${model.company} )""".update

    def create(model: FinancialsTransaction) =
      sql"""INSERT INTO master_compta (ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT ) VALUES
          (${model.tid}, ${model.oid}, ${model.costcenter}, ${model.account},${model.transdate},
          ${model.postingdate}, ${model.enterdate}, ${model.period}, ${model.posted}, ${model.modelid},
         ${model.company}, ${model.text},  ${model.typeJournal},  ${model.file_content} ) """.update

    def create2(model: FinancialsTransaction): ConnectionIO[List[Int]] =
      for {
        transid <- sql"SELECT NEXTVAL('master_compta_id_seq')".query[Long].unique
        trs <- create(model.copy(tid = transid)).run
        lines <- model.lines.traverse(createDetails(_, transid).run)
      } yield (lines ++ List(trs))

    def create3(model: FinancialsTransaction): ConnectionIO[List[Int]] =
      for {
        transId <- sql"SELECT NEXTVAL('master_compta_id_seq')".query[Long].unique
        trs <- create(model.copy(tid = transId)).run
        lines_ <- SQL.FinancialsTransactionDetails.getByTransId(model.tid,model.company).to[List]
        customer <-  SQL.Customer.getByAccount(model.account,model.company).to[List]
        supplier <-  SQL.Supplier.getByAccount(model.account, model.company).option
        mappedLines =  if (model.modelid ==112) {
          lines_.map( line =>line.copy(account=supplier.headOption.map(_.oaccount).getOrElse("-1"), oaccount=line.account))
        } else {
          lines_.map( line =>line.copy(account=line.oaccount, oaccount=customer.headOption.map(_.oaccount).getOrElse("-1")))
        }
        lines <- mappedLines.traverse(createDetails(_, transId).run)
      } yield (lines ++ List(trs))

    def getBy(idx: String, company: String): Query0[FinancialsTransaction_Type2] = {
      val id = idx.toLong
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency
      FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id
     WHERE  A.id = $id AND COMPANY = ${company} ORDER BY  A.ID """.query

    }

    def findSome(company: String, model: String*): Query0[FinancialsTransaction_Type2] =
      // val id = model(0).toLong
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency WHERE A.COMPANY = ${company}
        ORDER BY A.ID ASC """.query

    def getByModelId(modelid: Int, company: String): Query0[FinancialsTransaction_Type2] =
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency
        FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id
        WHERE  A.modelid = $modelid AND A.COMPANY = ${company} ORDER BY A.ID ASC """.query

    def all2(company: String): Query0[FinancialsTransaction_Type] =
      sql"""SELECT TID, OID, COSTCENTER, ACCOUNT, TRANSDATE
        , POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid, COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
        FROM master_compta  WHERE COMPANY = ${company} ORDER BY ID  ASC""".query

    def list(company: String): Query0[FinancialsTransaction_Type2] =
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE
        , A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED, A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL
        , A.FILE_CONTENT, COALESCE (B.ID, -1) as ID , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
        ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
        , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
        , COALESCE (B.currency, 'EUR') as currency
        FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id
        WHERE A.COMPANY = ${company} ORDER BY id  ASC""".query

    def delete(id: String, company: String): Update0 = sql"""DELETE FROM master_compta WHERE ID = $id.toLong """.update

    override def update(models: List[FinancialsTransaction], company: String): List[Update0] = {
      println("UPDATING" + models.map(_.id));
      models.map(m => {
        println("UPDATING" + m);
        FinancialsTransactionDetails.update(m.lines, company);
        update(m, company)
      })
    }

    def getTransaction4Ids(ids: NonEmptyList[Long], company: String): Query0[FinancialsTransaction_Type] = {
     val q = fr"""SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE , POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid
        , COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
        FROM master_compta  WHERE COMPANY = ${company} AND """ ++ Fragments.in(fr"id", ids)
      q.query
    }

    def update2(model: FinancialsTransaction, company: String): Update0 = sql"""Update master_compta
    set OID=${model.oid}, COSTCENTER=${model.costcenter}, ACCOUNT=${model.account}, TRANSDATE=${model.transdate}
     , HEADERTEXT=${model.text}, FILE_CONTENT=${model.file_content}, TYPE_JOURNAL=${model.typeJournal}
    ,  PERIOD=${model.period}, POSTED=${model.posted}
     where id =${model.tid} AND POSTED=false AND COMPANY = ${company}""".update

    override def update(model: FinancialsTransaction, company: String): Update0 = sql"""Update master_compta
    set OID=${model.oid}, COSTCENTER=${model.costcenter}, ACCOUNT=${model.account}, TRANSDATE=${model.transdate}
     , HEADERTEXT=${model.text}, FILE_CONTENT=${model.file_content}, TYPE_JOURNAL=${model.typeJournal}
    ,  PERIOD=${model.period}
     where id =${model.tid} AND POSTED=false AND COMPANY = ${company}""".update
  }
  object Journal extends Repository[Journal, Journal] {
    def create(model: Journal): Update0 =
      sql"""INSERT INTO journal (ID,TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT ) VALUES
           (NEXTVAL('journal_id_seq'), ${model.transid}, ${model.oid}, ${model.account}, ${model.oaccount}
          , ${model.transdate}, ${model.enterdate}, ${model.postingdate}, ${model.period}, ${model.amount}
          , ${model.side}, ${model.company}, ${model.currency}, ${model.text}, ${model.month}, ${model.year}
          , ${model.modelid}, ${model.file_content}, ${model.typeJournal},${model.idebit}, ${model.debit}
          , ${model.icredit}, ${model.credit} )""".update

    def getBy(id: String, company: String): Query0[Journal] = sql"""
     SELECT  ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
     FROM journal
     WHERE id = $id.toLong AND COMPANY = ${company} ORDER BY  id ASC """.query

    def findSome(company: String, model: String*): Query0[Journal] = {
      val acc: String = model(0)
      val fromPeriod: Int = model(1).toInt
      val toPeriod: Int = model(2).toInt
      //println("acc: " + acc)
      //println("fromPeriod: " + fromPeriod)
      //println("toPeriod: " + toPeriod)
      sql"""SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
        , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
        FROM journal
        WHERE ACCOUNT = ${acc} AND PERIOD BETWEEN ${fromPeriod} AND ${toPeriod}  AND COMPANY = ${company}
        ORDER BY  id ASC """.query
    }

    def getByModelId(modelid: Int, company: String): Query0[Journal] =
      sql""" SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
    FROM journal  Where modelid = $modelid AND COMPANY = ${company} ORDER BY  ID ASC """.query

    def list(company: String): Query0[Journal] =
      sql"""SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
     FROM journal WHERE COMPANY = ${company} ORDER BY id  ASC
  """.query

    def delete(id: String, company: String): Update0 = sql"""DELETE FROM dual  """.update

    def update(model: Journal, company: String): Update0 = sql"""Update dual set column1=true""".update
  }
  object BankStatement extends Repository[BankStatement, BankStatement] {

    //def insertSQL =
    //  Update[BankStatement]("INSERT INTO bankstatement VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)", None)
    def create(item: BankStatement): Update0 =
      sql"""INSERT INTO bankstatement (ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE, BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO, COMPANY, COMPANYIBAN, POSTED, modelid) VALUES
     ( NEXTVAL('bankstatement_id_seq'),  ${item.depositor}, ${item.postingdate}, ${item.valuedate}, ${item.postingtext}
     , ${item.purpose}, ${item.beneficiary},  ${item.accountno}, ${item.bankCode}, ${item.amount}, ${item.currency}
     , ${item.info}, ${item.company}, ${item.companyIban}, ${item.posted}, ${item.modelid} )""".update

    def getBy(id: String, company: String): Query0[BankStatement] = sql"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE, POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE id = $id.toLong ORDER BY  id ASC
     """.query

    def getBy(id: Long): Query0[BankStatement] = sql"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE id = $id """.query

    def getPayment(ids: NonEmptyList[Long]): Query0[BankStatement] = {
      val q = fr"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE   POSTED =false AND AMOUNT <0   AND """ ++ Fragments.in(fr"id", ids)
      q.query
    }

    def getSettlement(ids: NonEmptyList[Long]): Query0[BankStatement] = {
      val q = fr"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE   POSTED =false AND AMOUNT >0   AND """ ++ Fragments.in(fr"id", ids)
      q.query
    }

    def getByModelId(modelid: Int, company: String): Query0[BankStatement] = sql"""
        SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement  WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC
         """.query

    def list(company: String): Query0[BankStatement] = sql"""
     SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
     FROM bankstatement WHERE COMPANY = ${company} ORDER BY id  ASC""".query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM dual WHERE ID = $id AND COMPANY = ${company}""".update

    def findSome(company: String, model: String*): Query0[BankStatement] = {
      val posted: String = model(0)
      //val period: Boolean = model(1).toBoolean
      sql"""
         SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE, POSTINGTEXT, PURPOSE, BENEFICIARY
         , ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
         FROM bankstatement  WHERE POSTED=$posted AND COMPANY = ${company} ORDER BY id  ASC """.query
    }

    def update(item: BankStatement, company: String): Update0 =
      sql"""Update bankstatement set POSTINGTEXT=${item.postingtext} , PURPOSE=${item.purpose}
    ,  ACCOUNTNO= ${item.accountno}, BANKCODE=${item.bankCode}, INFO=${item.info}, POSTED=${item.posted}
    ,  COMPANYIBAN=${item.companyIban} where id =${item.bid} AND COMPANY = ${company}""".update

  }
  object Vat extends Repository[Vat, Vat] {

    def create(item: Vat): Update0 =
      sql"""INSERT INTO vat (ID, NAME, DESCRIPTION, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT, COMPANY, modelid)
             VALUES (${item.id}, ${item.name}, ${item.description}, ${item.percent}
            , ${item.inputVatAccount}, ${item.outputVatAccount}, ${item.company}, ${item.modelid} )""".update

    def getBy(id: String, company: String): Query0[Vat] = sql"""
     SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
     FROM vat WHERE id = $id AND COMPANY = ${company} ORDER BY  id ASC """.query

    def getByModelId(modelid: Int, company: String): Query0[Vat] = sql"""
        SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
        FROM vat  WHERE modelid = $modelid AND COMPANY = ${company} ORDER BY  id ASC """.query

    def findSome(company: String, model: String*): Query0[Vat] = sql"""
        SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
        , enter_date, modified_date, posting_date, company, modelid
        FROM vat   WHERE  COMPANY = ${company} ORDER BY  id ASC""".query

    def list(company: String): Query0[Vat] = sql"""
     SELECT id, name,description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
     , enter_date, modified_date, posting_date, company, modelid
    FROM vat WHERE  COMPANY = ${company} ORDER BY id  ASC""".query

    def delete(id: String, company: String): Update0 =
      sql"""DELETE FROM vat WHERE ID = $id AND COMPANY = ${company} """.update

    def update(model: Vat, company: String): Update0 =
      sql"""Update vat set  name =${model.name}, description=${model.description}
         ,  PERCENT=${model.percent}, INPUTVATACCOUNT=${model.inputVatAccount}
          , OUTPUTVATACCOUNT=${model.outputVatAccount}, company=${model.company}
         where id =${model.id} AND COMPANY = ${company}""".update
  }
}
