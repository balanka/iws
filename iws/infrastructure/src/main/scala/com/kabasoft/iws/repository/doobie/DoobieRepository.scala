package com.kabasoft.iws.repository.doobie

import java.time.Instant
import java.util.Date

import cats.Monad
import cats.effect.{IO, Sync}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import com.kabasoft.iws._
import com.kabasoft.iws.domain._
import com.kabasoft.iws.service.Repository
import com.kabasoft.iws.domain.FinancialsTransaction.{FinancialsTransaction_Type, FinancialsTransaction_Type2}
import com.kabasoft.iws.repository.doobie.SQL.FinancialsTransactionDetailsRepo
import com.kabasoft.iws.repository.doobie.SQLPagination._

private object SQL {

  object Article {
    def create(item: Article): Update0 =
      sql"""INSERT INTO article (ID, NAME, DESCRIPTION, MODELID, PARENT, PRICE, STOCKED) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.parent},  ${item.price},  ${item.stocked})""".update

    def select(id: String): Query0[Article] = sql"""
     SELECT id, name, description, modelId, price, parent, stocked
     FROM article
     WHERE id = $id ORDER BY  id ASC
    """.query

    def findByModelId(modelid: Int): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article
     WHERE modelid = $modelid ORDER BY  id ASC
    """.query
    def findSome(model: String*): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article
     ORDER BY  id ASC """.query

    def all: Query0[Article] = sql"""
     SELECT id, name, description, modelid, parent,  price, stocked
     FROM article
     ORDER BY id ASC
     """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM article WHERE ID = $id""".update

    def update(model: Article): Update0 = sql"""
         Update article set id = ${model.id}, name =${model.name} description =${model.description}
         parent =${model.parent},  price =${model.price} , stocked=${model.stocked}  where id =${model.id}
         """.update
  }
  object Routes {

    def create(item: Routes): Update0 =
      sql"""INSERT INTO Routes (ID, NAME, DESCRIPTION, MODELID, PARENT) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.component} )""".update

    def select(id: String): Query0[Routes] = sql"""
     SELECT id, name, description, modelid, parent as component
     FROM Routes
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[Routes] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes  WHERE modelid = $modelid ORDER BY  id ASC
         """.query
    def findSome(model: String*): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes   ORDER BY  id ASC""".query

    def all: Query0[Routes] = sql"""
     SELECT id, name,description, modelid, parent as component
     FROM Routes
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM Routes WHERE ID = $id""".update

    def update(model: Routes): Update0 = sql"""Update Routes set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.component}  where id =${model.id}""".update
  }
  object Masterfile {

    def create(item: Masterfile): Update0 =
      sql"""INSERT INTO masterfiles (ID, NAME, DESCRIPTION, modelid, PARENT) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.parent} )""".update

    def select(id: String): Query0[Masterfile] = sql"""
     SELECT id, name, description, modelid, parent
     FROM masterfiles
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles   ORDER BY  id ASC""".query

    def all: Query0[Masterfile] = sql"""
     SELECT id, name,description, modelid, parent
     FROM masterfiles
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM masterfiles WHERE ID = $id""".update

    def update(model: Masterfile): Update0 = sql"""Update masterfiles set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.parent}  where id =${model.id}""".update
  }
  object CostCenter {

    def create(item: CostCenter): Update0 =
      sql"""INSERT INTO costcenter (ID, NAME, DESCRIPTION, modelid, ACCOUNT, COMPANY) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.account}, ${item.company} )""".update

    def select(id: String): Query0[CostCenter] = sql"""
     SELECT id, name, description, modelid, ACCOUNT, company, enter_date, modified_date, posting_date
     FROM costcenter
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[CostCenter] = sql"""
        SELECT id, name, description, modelid, ACCOUNT, company, enter_date, modified_date, posting_date
        FROM costcenter  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[CostCenter] = sql"""
        SELECT id, name, description, modelid, ACCOUNT, company, enter_date, modified_date, posting_date
        FROM costcenter   ORDER BY  id ASC""".query

    def all: Query0[CostCenter] = sql"""
     SELECT id, name,description, modelid, ACCOUNT, company, enter_date, modified_date, posting_date
     FROM costcenter
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM costcenter WHERE ID = $id""".update

    def update(model: CostCenter): Update0 = sql"""Update costcenter set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}, company=${model.company}   where id =${model.id}""".update
  }
  object Account {
    def create(item: Account): Update0 =
      sql"""INSERT INTO account (ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company
             , modelid, ACCOUNT, isDebit, balancesheet) VALUES  (${item.id.value}, ${item.name}, ${item.description}
             , ${item.postingdate}, ${item.changedate}, ${item.enterdate}, ${item.company}, ${item.modelid}
             , ${item.account}, ${item.isDebit}, ${item.balancesheet})""".update

    def select(id: String): Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet
     FROM account
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[Account] = sql"""
        SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet
        FROM account WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet FROM account ORDER BY id ASC """.query

    def all: Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet FROM account ORDER BY id ASC """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM account WHERE ID = $id""".update

    def update(model: Account): Update0 =
      sql"""Update account set  NAME =${model.name}, DESCRIPTION=${model.description}
        , posting_date =${model.postingdate}, modified_date =${model.changedate}, enter_date = ${model.enterdate}
        , company =${model.company}, account =${model.account}, isDebit =${model.isDebit}, balancesheet =${model.balancesheet}
         where id =${model.id.value} """.update

  }
  object PeriodicAccountBalance {
    def create(item: PeriodicAccountBalance): Update0 =
      sql"""INSERT INTO periodic_account_balance (ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid) VALUES (
              ${item.id}, ${item.account}, ${item.period}, ${item.idebit}, ${item.icredit}, ${item.debit}, ${item.credit}
             , ${item.company}, ${item.currency}, ${item.modelid} )""".update

    def select(id: String): Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
     FROM periodic_account_balance
     WHERE id = $id ORDER BY  PERIOD ASC
     """.query

    def findByModelId(modelid: Int): Query0[PeriodicAccountBalance] = sql"""
        SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE modelid = $modelid ORDER BY  PERIOD ASC
         """.query

    def findSome(model: String*): Query0[PeriodicAccountBalance] = {
      val acc: String = model(0)
      val fromPeriod: Int = model(1).toInt
      val toPeriod: Int = model(2).toInt
      println("account", acc)
      println("fromPeriod", fromPeriod)
      println("toPeriod", toPeriod)

      sql"""SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
        FROM periodic_account_balance WHERE ACCOUNT = $acc AND PERIOD BETWEEN $fromPeriod AND $toPeriod
        ORDER BY  ID ASC """.query
    }

    def all: Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
     FROM periodic_account_balance
     ORDER BY PERIOD ASC """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM periodic_account_balance WHERE ID = $id""".update

    def update(model: PeriodicAccountBalance): Update0 = sql"""Update periodic_account_balance
         set IDEBIT=${model.idebit}, ICREDIT=${model.icredit}, DEBIT=${model.debit}, CREDIT=${model.credit}
         , COMPANY=${model.company}, CURRENCY ={model.company} where id =${model.id} """.update

    def findSome(model: PeriodicAccountBalance) = sql"""
      SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
      FROM periodic_account_balance
      WHERE ACCOUNT=${model.account} and PERIOD BETWEEN ${model.period} and ${model.period}
      ORDER BY PERIOD ASC """.query
  }
  object Customer {

    def create(item: Customer): Update0 =
      sql"""INSERT INTO customer (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY,
            PHONE, EMAIL, ACCOUNT, REVENUE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES
     (${item.id.value}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
     , ${item.state},  ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
     , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def select(id: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY,modelid,  enter_date, modified_date, posting_date
     FROM customer
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer   ORDER BY  id ASC
         """.query

    def all: Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid, enter_date, modified_date, posting_date
     FROM customer
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM customer WHERE ID = $id""".update

    def update(model: Customer): Update0 =
      sql"""Update customer set  name =${model.name}, description=${model.description}
         , street=${model.street}, city=${model.city}, state = ${model.state}, zip=${model.zip}, , country=${model.country}
         , phone= ${model.phone},  email= ${model.email}, account=${model.account},  revenue_account=${model.oaccount}
         , iban =${model.iban}, vatcode=${model.vatcode}  company=${model.company} where id =${model.id}""".update
  }
  object Supplier {

    def create(item: Supplier): Update0 =
      sql"""INSERT INTO supplier (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY
            PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES(
            ${item.id.value}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
           , ${item.state}, ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
           , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def select(id: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[Supplier] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
        FROM supplier  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def all: Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
     , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier ORDER BY id  ASC """.query

    def findSome(model: String*): Query0[Supplier] = sql"""
      SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
    , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
    FROM supplier ORDER BY id  ASC """.query
    def delete(id: String): Update0 = sql"""DELETE FROM dual WHERE ID = $id""".update

    def update(model: Supplier): Update0 =
      sql"""Update supplier set name =${model.name}, description=${model.description}
         , street=${model.street}, city=${model.city},state = ${model.state}, zip=${model.zip}, country=${model.country}
         , phone= ${model.phone}, email= ${model.email}, account=${model.account}, charge_account=${model.oaccount}
         , iban =${model.iban}, vatcode=${model.vatcode}, company=${model.company}  where id =${model.id} """.update

  }
  object FinancialsTransactionDetailsRepo {

    def create(model: FinancialsTransactionDetails): Update0 =
      sql"""INSERT INTO details_compta (ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT,DUEDATE, TEXT, CURRENCY, COMPANY,TERMS) VALUES
     (SELECT nextval('details_compta_Id_seq'), ${model.transid}, ${model.account}, ${model.side}, ${model.oaccount}, ${model.amount}
    , ${model.duedate}, ${model.text},  ${model.currency}, ${model.company} )""".update

    def select(id: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE id = $id ORDER BY  id ASC """.query

    def findSome(model: String*): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta ORDER BY  id ASC """.query

    def findByModelId(modelid: Int): Query0[FinancialsTransactionDetails] = sql"""
        SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
        FROM details_compta  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def all: Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM details_compta WHERE ID = $id""".update

    def update(model: FinancialsTransactionDetails): Update0 = sql"""Update details_compta
     set TRANSID=${model.transid}, ACCOUNT=${model.account}, SIDE=${model.side}, OACCOUNT=${model.oaccount},
     AMOUNT=${model.amount}, DUEDATE=${model.duedate}, TEXT=${model.text},  CURRENCY=${model.currency}, COMPANY=${model.company}
     where id =${model.id}""".update
  }
  object FinancialsTransactionRepo {

    def create(model: FinancialsTransaction): Update0 =
      sql"""INSERT INTO master_compta (ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT ) VALUES
          (SELECT nextval('master_compta_Id_seq'), ${model.oid}, ${model.costcenter}, ${model.account},${model.transdate},
          ${model.postingdate}, ${model.enterdate}, ${model.period}, ${model.posted}, ${model.modelid},
         ${model.company}, ${model.text},  ${model.typeJournal},  ${model.file_content} )""".update

    def select(id: String): Query0[FinancialsTransaction] = sql"""
     SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta
     WHERE id = $id.toLong ORDER BY  id ASC """.query[FinancialsTransaction_Type].map(FinancialsTransaction.apply)

    def findSome(model: String*): Query0[FinancialsTransaction] = {
      val id = model(0).toLong
      sql"""SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta
     WHERE id = $id ORDER BY  id ASC """.query[FinancialsTransaction_Type].map(FinancialsTransaction.apply)
    }

    def findByModelId(modelid: Int): Query0[FinancialsTransaction_Type2] = sql"""
    SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED, A.modelid,
       A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, B.ID, B.account,
       B.side, B.oaccount, B.amount, B.duedate, B.text, B.currency, B.terms
    FROM master_compta A, details_compta B  WHERE A.id = B.transid and A.modelid = $modelid ORDER BY  A.ID ASC """.query

    def all: Query0[FinancialsTransaction_Type] = sql"""
     SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM master_compta WHERE ID = $id.toLong """.update

    def update(model: FinancialsTransaction): Update0 = sql"""Update master_compta
    set OID=${model.oid}, COSTCENTER=${model.costcenter}, ACCOUNT=${model.account}, HEADERTEXT=${model.text},
     TRANSDATE=${model.transdate}, FILE_CONTENT=${model.file_content} TYPE_JOURNAL=${model.typeJournal}
    , COMPANY=${model.company} , modelid=${model.modelid}, PERIOD=${model.period}
     where id =${model.id}""".update
  }
  object Journal {
    def create(model: Journal): Update0 =
      sql"""INSERT INTO journal (ID,TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT ) VALUES
          (SELECT nextval('journal_id_seq'), ${model.transid}, ${model.oid}, ${model.account}, ${model.oaccount}
          , ${model.transdate}, ${model.enterdate}, ${model.postingdate}, ${model.period}, ${model.amount}
          , ${model.side}, ${model.company}, ${model.currency}, ${model.text}, ${model.month}, ${model.year}
          , ${model.modelid}, ${model.file_content}, ${model.typeJournal},${model.idebit}, ${model.debit}
          , ${model.icredit}, ${model.credit} )""".update

    def select(id: String): Query0[Journal] = sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal
     WHERE id = $id.toLong ORDER BY  id ASC """.query

    def findSome(model: String*): Query0[Journal] = {
      val acc: String = model(0)
      val fromPeriod: Int = model(1).toInt
      val toPeriod: Int = model(2).toInt
      sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, TJOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal WHERE ACCOUNT = $acc AND PERIOD BETWEEN $fromPeriod AND toPeriod  ORDER BY  id ASC """.query
    }

    def findByModelId(modelid: Int): Query0[Journal] = sql"""
    SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
    FROM journal  Where modelid = $modelid ORDER BY  ID ASC """.query

    def all: Query0[Journal] = sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM dual  """.update

    def update(model: Journal): Update0 = sql"""Update dual set column1=true""".update
  }
  object BankStatement {

    def create(item: BankStatement): Update0 =
      sql"""INSERT INTO bankstatement (ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid) VALUES
     (SELECT nextval('bankstatement_id_seq'), ${item.id}, ${item.depositor}, ${item.postingdate}, ${item.valuedate}
    , {item.postingtext}, {item.purpose},${item.beneficiary},  ${item.accountno},{item.bankCode}, {item.amount}
    , {item.currency}, {item.info}, {item.company}, {item.companyIban}, {item.posted}, {item.modelid} """.update

    def select(id: String): Query0[BankStatement] = sql"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE id = $id.toLong ORDER BY  id ASC
     """.query

    def findByModelId(modelid: Int): Query0[BankStatement] = sql"""
        SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def all: Query0[BankStatement] = sql"""
     SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
     FROM bankstatement ORDER BY id  ASC""".query

    def delete(id: String): Update0 = sql"""DELETE FROM dual WHERE ID = $id""".update

    def findSome(model: String*): Query0[BankStatement] = {
      val posted: String = model(0)
      val period: Boolean = model(1).toBoolean
      sql"""
         SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE, POSTINGTEXT, PURPOSE, BENEFICIARY
         , ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
         FROM bankstatement  WHERE POSTED=$posted ORDER BY id  ASC """.query
    }

    def update(item: BankStatement): Update0 =
      sql"""Update bankstatement set id = ${item.id}, DEPOSITOR=${item.depositor}
      POSTINGDATE= ${item.postingdate}, VALUDATE= ${item.valuedate},POSTINGTEXT= {item.postingtext}
    , PURPOSE={item.purpose},  BENEFICIARY=${item.beneficiary}, ACCOUNTNO= ${item.accountno}, BANKCODE={item.bankCode}
    , AMOUNT={item.amount}, CURRENCY={item.currency}, INFO={item.info}, COMAPNY={item.company}, COMPANYIBAN={item.companyIban}
    , POSTED={item.posted}, {item.modelid} where id =${item.id}""".update
  }
  object Vat {

    def create(item: Vat): Update0 =
      sql"""INSERT INTO vat (ID, NAME, DESCRIPTION, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT, COMPANY, modelid)
             VALUES (${item.id.value}, ${item.name}, ${item.description}, ${item.percent}
            , ${item.inputVatAccount}, ${item.outputVatAccount}, ${item.company}, ${item.modelid} )""".update

    def select(id: String): Query0[Vat] = sql"""
     SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
     FROM vat WHERE id = $id ORDER BY  id ASC """.query

    def findByModelId(modelid: Int): Query0[Vat] = sql"""
        SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
        FROM vat  WHERE modelid = $modelid ORDER BY  id ASC """.query

    def all: Query0[Vat] = sql"""
     SELECT id, name,description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
     , enter_date, modified_date, posting_date, company, modelid
    FROM vat ORDER BY id  ASC""".query

    def delete(id: String): Update0 = sql"""DELETE FROM vat WHERE ID = $id""".update

    def update(model: Vat): Update0 = sql"""Update vat set  name =${model.name}, description=${model.description}
         ,  PERCENT=${model.percent}, INPUTVATACCOUNT=${model.inputVatAccount}
          , OUTPUTVATACCOUNT=${model.outputVatAccount}, company=${model.company}
         where id =${model.id}""".update
  }

}
//class MyClass extends MyTrait[({ type l[A] = Map[String, A] })#l] λ[A => X[A, Throwable]]
//final case class DoobiePetRepository[F[_]: Bracket[?[_], Throwable]](transactor: Transactor[F])
/*
class Foo[A[_,_],B] {
  type AB[C] = A[B, C]
  def apply(functor: Functor[AB]) = ...
}
 */

final case class DoobieRoutesRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Routes] {

  import SQL._

  def create(pet: Routes): F[Int] = SQL.Routes.create(pet).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Routes.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Routes]] =
    paginate(until - from, from)(Routes.all)
      .to[List]
      .transact(transactor)

  override def getBy(id: String): F[Option[Routes]] = Routes.select(id).option.transact(transactor)

  def update(model: Routes): F[Int] = SQL.Routes.update(model).run.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Routes]] =
    paginate(until - from, from)(Routes.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Routes]] = list(0, 1000000)
}

object DoobieRoutesRepository {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieRoutesRepository[F] =
    new DoobieRoutesRepository[F](transactor)
}

final case class DoobieMasterfileRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Masterfile] {

  import SQL._

  def create(model: Masterfile): F[Int] = {
    println("SQL.Masterfile.update(model) >>", SQL.Masterfile.create(model).sql);
    println("model >>", model.toString);
    SQL.Masterfile.create(model).run.transact(transactor)
  }

  def delete(id: String): F[Int] = SQL.Masterfile.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(Masterfile.all)
      .to[List]
      .transact(transactor)

  override def getBy(id: String): F[Option[Masterfile]] = Masterfile.select(id).option.transact(transactor)

  def update(model: Masterfile): F[Int] = {
    println("SQL.Masterfile.update(model) >>", SQL.Masterfile.update(model).sql);
    println("model >>", model.toString);
    SQL.Masterfile.update(model).run.transact(transactor)
  }

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(Masterfile.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Masterfile]] = list(0, 1000000)
}
object DoobieMasterfileRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieMasterfileRepository[F] =
    new DoobieMasterfileRepository[F](transactor)
}

final case class DoobieCostCenterRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, CostCenter] {

  import SQL._

  def create(cc: CostCenter): F[Int] = SQL.CostCenter.create(cc).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.CostCenter.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[CostCenter]] =
    paginate(until - from, from)(CostCenter.all)
      .to[List]
      .transact(transactor)

  override def getBy(id: String): F[Option[CostCenter]] = CostCenter.select(id).option.transact(transactor)

  def update(model: CostCenter): F[Int] = SQL.CostCenter.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[CostCenter]] =
    paginate(until - from, from)(CostCenter.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[CostCenter]] = list(0, 1000000)
}

object DoobieCostCenterRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieCostCenterRepository[F] =
    new DoobieCostCenterRepository[F](transactor)
}

final case class DoobieAccountRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Account] {

  import SQL._

  def create(item: Account): F[Int] = {
    println("create Account item", item);
    println("create Account", SQL.Account.create(item).sql);
    SQL.Account.create(item).run.transact(transactor)
  }

  def delete(id: String): F[Int] = SQL.Account.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(Account.all)
      .to[List]
      .transact(transactor)

  def update(model: Account): F[Int] = {
    println("SQL.Account.update(model) >>", SQL.Account.update(model).sql);
    println("model>>", model.toString);
    SQL.Account.update(model).run.transact(transactor)
  }

  override def getBy(id: String): F[Option[Account]] = Account.select(id).option.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Account]] = {
    println("findByModelId", Account.findByModelId(modelid).sql)
    paginate(until - from, from)(Account.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  }

  def findSome(model: String*): F[List[Account]] = list(0, 1000000)

}

object DoobieAccountRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieAccountRepository[F] =
    new DoobieAccountRepository[F](transactor)
}

final case class DoobieArticleRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Article] {

  def create(item: Article): F[Int] = SQL.Article.create(item).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.Article.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.all)
      .to[List]
      .transact(transactor)

  def update(model: Article): F[Int] = SQL.Article.update(model).run.transact(transactor)

  override def getBy(id: String): F[Option[Article]] = SQL.Article.select(id).option.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Article]] =
    paginate(until - from, from)(SQL.Article.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Article]] = list(0, 1000000)
}

object DoobieArticleRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieArticleRepository[F] =
    new DoobieArticleRepository[F](transactor)
}

final case class DoobieCustomerRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Customer] {

  import SQL._

  def create(customer: Customer): F[Int] = SQL.Customer.create(customer).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.Customer.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Customer]] = {
    println("Customer.all >>", Customer.all.sql);
    paginate(until - from, from)(Customer.all).to[List].transact(transactor)
  }

  override def getBy(id: String): F[Option[Customer]] = Customer.select(id).option.transact(transactor)

  def update(model: Customer): F[Int] = SQL.Customer.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Customer]] = {
    println("Customer.getByModelId >>>>" + modelid, Customer.findByModelId(modelid).sql);
    paginate(until - from, from)(Customer.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  }

  def findSome(model: String*): F[List[Customer]] = list(0, 1000000)
}

object DoobieCustomerRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieCustomerRepository[F] =
    new DoobieCustomerRepository[F](transactor)
}

final case class DoobiePeriodicAccountBalanceRepository[F[_]: Sync](transactor: Transactor[F])
    extends Repository[F, PeriodicAccountBalance] {

  import SQL._

  def create(balance: PeriodicAccountBalance): F[Int] =
    SQL.PeriodicAccountBalance.create(balance).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.PeriodicAccountBalance.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(PeriodicAccountBalance.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[PeriodicAccountBalance]] =
    PeriodicAccountBalance.select(id).option.transact(transactor)

  def update(model: PeriodicAccountBalance): F[Int] =
    SQL.PeriodicAccountBalance.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(PeriodicAccountBalance.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[PeriodicAccountBalance]] = {
    println("PeriodicAccountBalance.findSome(model: _*)", SQL.PeriodicAccountBalance.findSome(model: _*).sql);
    println("model: String*", model);
    PeriodicAccountBalance
      .findSome(model: _*)
      .to[List]
      .transact(transactor)
  }
}

object DoobiePeriodicAccountBalanceRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobiePeriodicAccountBalanceRepository[F] =
    new DoobiePeriodicAccountBalanceRepository[F](transactor)
}

final case class DoobieSupplierRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Supplier] {

  import SQL._

  def create(supplier: Supplier): F[Int] = SQL.Supplier.create(supplier).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.Supplier.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Supplier]] =
    paginate(until - from, from)(Supplier.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[Supplier]] = Supplier.select(id).option.transact(transactor)

  def update(model: Supplier): F[Int] = {
    println("SQL.Supplier.update(model) >>", SQL.Supplier.update(model).sql);
    println("model>>", model.toString);
    SQL.Supplier.update(model).run.transact(transactor)
  }

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Supplier]] =
    //SQL.Supplier.findByModelId(modelid).to[List].transact(transactor)
    paginate(until - from, from)(Supplier.findByModelId(modelid))
    //.map(Supplier.apply)
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Supplier]] = list(0, 1000000)
}

object DoobieSupplierRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieSupplierRepository[F] =
    new DoobieSupplierRepository[F](transactor)
}

final case class DoobieFinancialsTransactionDetailsRepository[F[_]: Sync](transactor: Transactor[F])
    extends Repository[F, FinancialsTransactionDetails] {

  import SQL._

  def create(details: FinancialsTransactionDetails): F[Int] =
    FinancialsTransactionDetailsRepo.create(details).run.transact(transactor)

  def delete(id: String): F[Int] = FinancialsTransactionDetailsRepo.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[FinancialsTransactionDetails]] =
    paginate(until - from, from)(FinancialsTransactionDetailsRepo.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[FinancialsTransactionDetails]] =
    FinancialsTransactionDetailsRepo.select(id).option.transact(transactor)

  def update(model: FinancialsTransactionDetails): F[Int] =
    SQL.FinancialsTransactionDetailsRepo.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransactionDetails]] = {
    println(
      "FinancialsTransactionDetailsRepo.findByModelId(modelid)",
      FinancialsTransactionDetailsRepo.findByModelId(modelid).sql
    );

    paginate(until - from, from)(FinancialsTransactionDetailsRepo.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  }

  def findSome(model: String*): F[List[FinancialsTransactionDetails]] = //list(0, 1000000)
    FinancialsTransactionDetailsRepo.findSome(model: _*).to[List].transact(transactor)
}

object DoobieFinancialsTransactionDetailsRepository {
  def apply[Id[_]: Sync](transactor: Transactor[Id]): DoobieFinancialsTransactionDetailsRepository[Id] =
    new DoobieFinancialsTransactionDetailsRepository[Id](transactor)

  // def findSome[Id[_]: Sync](transactor: Transactor[Id], id: String): Id[List[FinancialsTransactionDetails]] =
  //   FinancialsTransactionDetailsRepo.findSome(id).to[List].transact(transactor)
}

final case class DoobieFinancialsTransactionRepository[F[_]: Sync: Monad](transactor: Transactor[F])
    extends Repository[F, FinancialsTransaction] {

  import SQL._

  def create(transaction: FinancialsTransaction): F[Int] =
    SQL.FinancialsTransactionRepo.create(transaction).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.FinancialsTransactionRepo.delete(id).run.transact(transactor)

  override def getBy(id: String): F[Option[FinancialsTransaction]] =
    FinancialsTransactionRepo.select(id).option.transact(transactor)

  def update(model: FinancialsTransaction): F[Int] =
    SQL.FinancialsTransactionRepo.update(model).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(FinancialsTransactionRepo.all)
      .map(FinancialsTransaction.apply)
      //.map(x => x.copy(lines = f(x.tid.toString)))
      .to[List]
      .transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransaction]] =
    paginate(until - from, from)(FinancialsTransactionRepo.findByModelId(modelid))
    //.map(FinancialsTransaction.apply)
      .to[List]
      .map(FinancialsTransaction.apply)
      .transact(transactor)

  def findSome(model: String*): F[List[FinancialsTransaction]] = list(0, 1000000)

}

object DoobieFinancialsTransactionRepository {
  def apply[F[_]: Sync: Monad](transactor: Transactor[F]): DoobieFinancialsTransactionRepository[F] =
    new DoobieFinancialsTransactionRepository[F](transactor)
}

final case class DoobieJournalRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Journal] {

  import SQL._

  def create(journal: Journal): F[Int] = SQL.Journal.create(journal).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.Journal.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Journal]] =
    paginate(until - from, from)(Journal.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[Journal]] = Journal.select(id).option.transact(transactor)

  def update(model: Journal): F[Int] = SQL.Journal.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Journal]] =
    paginate(until - from, from)(Journal.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Journal]] = SQL.Journal.findSome(model: _*).to[List].transact(transactor)
}

object DoobieJournalRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieJournalRepository[F] =
    new DoobieJournalRepository[F](transactor)
}

final case class DoobieBankStatementRepository[F[_]: Sync](transactor: Transactor[F])
    extends Repository[F, BankStatement] {

  import SQL._

  def create(bs: BankStatement): F[Int] = SQL.BankStatement.create(bs).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.BankStatement.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[BankStatement]] =
    paginate(until - from, from)(BankStatement.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[BankStatement]] = BankStatement.select(id).option.transact(transactor)

  def update(model: BankStatement): F[Int] = SQL.BankStatement.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[BankStatement]] =
    paginate(until - from, from)(BankStatement.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[BankStatement]] =
    SQL.BankStatement.findSome(model: _*).to[List].transact(transactor)
}

object DoobieBankStatementRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieBankStatementRepository[F] =
    new DoobieBankStatementRepository[F](transactor)
}
final case class DoobieVatRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Vat] {

  import SQL._

  def create(vat: Vat): F[Int] = SQL.Vat.create(vat).run.transact(transactor)

  def delete(id: String): F[Int] = SQL.Vat.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Vat]] =
    paginate(until - from, from)(Vat.all)
      .to[List]
      .transact(transactor)

  override def getBy(id: String): F[Option[Vat]] = Vat.select(id).option.transact(transactor)

  def update(model: Vat): F[Int] = SQL.Vat.update(model).run.transact(transactor)

  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Vat]] =
    paginate(until - from, from)(Vat.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(model: String*): F[List[Vat]] = list(0, 1000000)
}
object DoobieVatRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieVatRepository[F] = new DoobieVatRepository[F](transactor)
}
