package com.kabasoft.iws.repository.doobie

import java.time.Instant
import java.util.Date
import cats._
import cats.data._
import cats.Monad
import cats.effect.{IO, Sync}
import cats.implicits._
import cats.effect.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import com.kabasoft.iws._
import com.kabasoft.iws.domain._

import com.kabasoft.iws.domain.FinancialsTransaction.{FinancialsTransaction_Type, FinancialsTransaction_Type2}
import com.kabasoft.iws.domain.FinancialsTransactionDetails.FinancialsTransactionDetails_Type

import com.kabasoft.iws.repository.doobie.SQL.FinancialsTransactionDetails
import com.kabasoft.iws.repository.doobie.SQLPagination._

trait Repository[-A, B] {
  def create(item: A): Update0
  def delete(item: String): Update0
  def list: Query0[B]
  def getBy(id: String): Query0[B]
  def getByModelId(modelid: Int): Query0[B]
  def update(models: List[A]): List[Update0] = models.map(update)
  def update(model: A): Update0 //= update(List(model))
  def findSome(param: String*): Query0[B]
}
private object SQL {

  object Article extends Repository[Article, Article] {
    def create(item: Article): Update0 =
      sql"""INSERT INTO article (ID, NAME, DESCRIPTION, MODELID, PARENT, PRICE, STOCKED) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.parent},  ${item.price},  ${item.stocked})""".update

    def getBy(id: String): Query0[Article] = sql"""
     SELECT id, name, description, modelId, price, parent, stocked
     FROM article
     WHERE id = $id ORDER BY  id ASC
    """.query

    def getByModelId(modelid: Int): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article
     WHERE modelid = $modelid ORDER BY  id ASC
    """.query
    def findSome(model: String*): Query0[Article] = sql"""
     SELECT id, name, description, modelid, price, parent, stocked
     FROM article
     ORDER BY  id ASC """.query

    def list: Query0[Article] = sql"""
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
  object Bank extends Repository[Bank, Bank] {

    def create(item: Bank): Update0 =
      sql"""INSERT INTO Bank (ID, NAME, DESCRIPTION, MODELID) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid}} )""".update

    def getBy(id: String): Query0[Bank] = sql"""
     SELECT id, name, description, enter_date, modified_date, posting_date,modelid
     FROM Bank
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Bank] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid
        FROM Bank  WHERE modelid = $modelid ORDER BY  id ASC
         """.query
    def findSome(model: String*): Query0[Bank] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid as component
        FROM Bank   ORDER BY  id ASC""".query

    def list: Query0[Bank] = sql"""
     SELECT id, name,description, enter_date, modified_date, posting_date, modelid
     FROM Bank
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM Bank WHERE ID = $id""".update

    def update(model: Bank): Update0 = sql"""Update Bank set id = ${model.id}, name =${model.name},
         description=${model.description},   where id =${model.id}""".update
  }
  object Routes extends Repository[Routes, Routes] {

    def create(item: Routes): Update0 =
      sql"""INSERT INTO Routes (ID, NAME, DESCRIPTION, MODELID, PARENT) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.component} )""".update

    def getBy(id: String): Query0[Routes] = sql"""
     SELECT id, name, description, modelid, parent as component
     FROM Routes
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Routes] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes  WHERE modelid = $modelid ORDER BY  id ASC
         """.query
    def findSome(model: String*): Query0[Routes] = sql"""
        SELECT id, name, description, modelid, parent as component
        FROM Routes   ORDER BY  id ASC""".query

    def list: Query0[Routes] = sql"""
     SELECT id, name,description, modelid, parent as component
     FROM Routes
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM Routes WHERE ID = $id""".update

    def update(model: Routes): Update0 = sql"""Update Routes set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.component}  where id =${model.id}""".update
  }
  object Masterfile extends Repository[Masterfile, Masterfile] {
    def create(item: Masterfile): Update0 =
      sql"""INSERT INTO masterfiles (ID, NAME, DESCRIPTION, modelid, PARENT) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.parent} )""".update

    def getBy(id: String): Query0[Masterfile] = sql"""
     SELECT id, name, description, modelid, parent
     FROM masterfiles
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelid, parent
        FROM masterfiles   ORDER BY  id ASC""".query

    def list: Query0[Masterfile] = sql"""
     SELECT id, name,description, modelid, parent
     FROM masterfiles
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM masterfiles WHERE ID = $id""".update

    def update(model: Masterfile): Update0 = sql"""Update masterfiles set id = ${model.id}, name =${model.name},
         description=${model.description}, parent=${model.parent}  where id =${model.id}""".update
  }
  object CostCenter extends Repository[CostCenter, CostCenter] {

    def create(item: CostCenter): Update0 =
      sql"""INSERT INTO costcenter (ID, NAME, DESCRIPTION, modelid, ACCOUNT, COMPANY) VALUES
     (${item.id}, ${item.name}, ${item.description}
    , ${item.modelid},  ${item.account}, ${item.company} )""".update

    def getBy(id: String): Query0[CostCenter] = sql"""
     SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
     FROM costcenter
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[CostCenter] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
        FROM costcenter  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[CostCenter] = sql"""
        SELECT id, name, description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
        FROM costcenter   ORDER BY  id ASC""".query

    def list: Query0[CostCenter] = sql"""
     SELECT id, name,description, enter_date, modified_date, posting_date, modelid, ACCOUNT, company
     FROM costcenter
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM costcenter WHERE ID = $id""".update

    def update(model: CostCenter): Update0 = sql"""Update costcenter set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}, company=${model.company}   where id =${model.id}""".update
  }
  object Account extends Repository[Account, Account] {
    def create(item: Account): Update0 =
      sql"""INSERT INTO account (ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company
             , modelid, ACCOUNT, isDebit, balancesheet) VALUES  (${item.id}, ${item.name}, ${item.description}
             , ${item.postingdate}, ${item.changedate}, ${item.enterdate}, ${item.company}, ${item.modelid}
             , ${item.account}, ${item.isDebit}, ${item.balancesheet})""".update

    def getBy(id: String): Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet
     FROM account
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Account] = sql"""
        SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             modelid, ACCOUNT, isDebit, balancesheet
        FROM account WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet FROM account ORDER BY id ASC """.query

    def list: Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company, modelid, ACCOUNT
            ,  isDebit, balancesheet FROM account ORDER BY id ASC """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM account WHERE ID = $id""".update

    def update(model: Account): Update0 =
      sql"""Update account set  NAME =${model.name}, DESCRIPTION=${model.description}
        , posting_date =${model.postingdate}, modified_date =${model.changedate}, enter_date = ${model.enterdate}
        , company =${model.company}, account =${model.account}, isDebit =${model.isDebit}, balancesheet =${model.balancesheet}
         where id =${model.id} """.update

  }
  object PeriodicAccountBalance extends Repository[PeriodicAccountBalance, PeriodicAccountBalance] {
    def create(item: PeriodicAccountBalance): Update0 =
      sql"""INSERT INTO periodic_account_balance (ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid) VALUES (
              ${item.id}, ${item.account}, ${item.period}, ${item.idebit}, ${item.icredit}, ${item.debit}, ${item.credit}
             , ${item.company}, ${item.currency}, ${item.modelid} )""".update

    def getBy(id: String): Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIOD, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, modelid
     FROM periodic_account_balance
     WHERE id = $id ORDER BY  PERIOD ASC
     """.query

    def getByModelId(modelid: Int): Query0[PeriodicAccountBalance] = sql"""
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

    def list: Query0[PeriodicAccountBalance] = sql"""
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
  object Customer extends Repository[Customer, Customer] {

    def create(item: Customer): Update0 =
      sql"""INSERT INTO customer (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY,
            PHONE, EMAIL, ACCOUNT, REVENUE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES
     (${item.id}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
     , ${item.state},  ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
     , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def getBy(id: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY,modelid,  enter_date, modified_date, posting_date
     FROM customer
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def findSome(model: String*): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT,REVENUE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,  enter_date, modified_date, posting_date
        FROM customer   ORDER BY  id ASC
         """.query

    def list: Query0[Customer] = sql"""
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
  object Supplier extends Repository[Supplier, Supplier] {

    def create(item: Supplier): Update0 =
      sql"""INSERT INTO supplier (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY
            PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT, IBAN, VATCODE, COMPANY, modelid ) VALUES(
            ${item.id}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
           , ${item.state}, ${item.zip},  ${item.country}, ${item.phone},  ${item.email}, ${item.account}
           , ${item.oaccount}, ${item.iban}, ${item.vatcode}, ${item.company} , ${item.modelid})""".update

    def getBy(id: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
     FROM supplier
     WHERE id = $id ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[Supplier] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, COUNTRY, PHONE, EMAIL, ACCOUNT, CHARGE_ACCOUNT
             , IBAN, VATCODE, COMPANY, modelid,   enter_date, modified_date, posting_date
        FROM supplier  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def list: Query0[Supplier] = sql"""
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
  object FinancialsTransactionDetails extends Repository[FinancialsTransactionDetails, FinancialsTransactionDetails] {

    def create(models: List[FinancialsTransactionDetails]): List[Update0] = models.map(m => create(m))

    def create(model: FinancialsTransactionDetails): Update0 =
      sql"""INSERT INTO details_compta (ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT,DUEDATE, TEXT, CURRENCY, COMPANY) VALUES
     (nextval('details_compta_id_seq'), ${model.transid}, ${model.account}, ${model.side}, ${model.oaccount}, ${model.amount}
    , ${model.duedate}, ${model.text},  ${model.currency}, ${model.company} )""".update

    def getBy(id: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE id = $id ORDER BY  id ASC """.query

    def findSome(model: String*): Query0[FinancialsTransactionDetails] = {
      val transid = model(0).toLong
      sql"""SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta  WHERE TRANSID =${transid} ORDER BY  id ASC """.query[FinancialsTransactionDetails]
    }

    def getByModelId(modelid: Int): Query0[FinancialsTransactionDetails] = sql"""
        SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
        FROM details_compta  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def list: Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta ORDER BY id  ASC
  """.query

    def delete(ids: List[String]): List[Update0] = { println("DELETING", ids); ids.map(delete) }

    def delete(idx: String): Update0 = sql"""DELETE FROM details_compta WHERE ID = ${idx.toLong}""".update

    def SQL_UPDATE(model: FinancialsTransactionDetails) = sql"""Update details_compta
     set TRANSID=${model.transid}, ACCOUNT=${model.account}, SIDE=${model.side}, OACCOUNT=${model.oaccount},
     AMOUNT=${model.amount}, DUEDATE=${model.duedate}, TEXT=${model.text},  CURRENCY=${model.currency}, COMPANY=${model.company}
     where id =${model.lid}"""

    override def update(models: List[FinancialsTransactionDetails]): List[Update0] = {
      println("UPDATING", models.map(_.lid)); models.map(m => { println("UPDATING", update(m).sql); update(m) })
    }

    def update(model: FinancialsTransactionDetails): Update0 = sql"""Update details_compta
     set ACCOUNT=${model.account}, SIDE=${model.side}, OACCOUNT=${model.oaccount},
     AMOUNT=${model.amount}, DUEDATE=${model.duedate}, TEXT=${model.text},  CURRENCY=${model.currency}
     where id =${model.lid} """.update
  }
  object FinancialsTransaction extends Repository[FinancialsTransaction, FinancialsTransaction_Type2] {

    def create(model: FinancialsTransaction): Update0 =
      sql"""INSERT INTO master_compta (ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT ) VALUES
          (NEXTVAL('master_compta_id_seq'), ${model.oid}, ${model.costcenter}, ${model.account},${model.transdate},
          ${model.postingdate}, ${model.enterdate}, ${model.period}, ${model.posted}, ${model.modelid},
         ${model.company}, ${model.text},  ${model.typeJournal},  ${model.file_content} ) """.update

    def getBy(idx: String): Query0[FinancialsTransaction_Type2] = {
      val id = idx.toLong
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency
      FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id
     WHERE  A.id = $id ORDER BY  A.ID """.query

      // .query[FinancialsTransaction_Type2]
      // .map(FinancialsTransaction.apply)
    }

    def findSome(model: String*): Query0[FinancialsTransaction_Type2] = {
      val id = model(0).toLong
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency
        ORDER BY A.ID ASC """.query
    }
    /*
    def findByModelId2(modelid: Int): Query0[FinancialsTransaction] = sql"""
    SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid,
       COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
    FROM master_compta   WHERE  modelid = ${modelid} ORDER BY  ID ASC """.query[FinancialsTransaction]

     */

    def getByModelId(modelid: Int): Query0[FinancialsTransaction_Type2] =
      sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED
        , A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, COALESCE (B.ID, -1) as ID
      , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
     ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
     , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
     , COALESCE (B.currency, 'EUR') as currency
        FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id
        WHERE  A.modelid = $modelid ORDER BY A.ID ASC """.query

    def all2: Query0[FinancialsTransaction_Type] = sql"""SELECT TID, OID, COSTCENTER, ACCOUNT, TRANSDATE
        , POSTINGDATE, ENTERDATE, PERIOD, POSTED, modelid, COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
        FROM master_compta  ORDER BY ID  ASC""".query

    def list: Query0[FinancialsTransaction_Type2] = sql"""SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE
        , A.POSTINGDATE, A.ENTERDATE, A.PERIOD, A.POSTED, A.modelid, A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL
        , A.FILE_CONTENT, COALESCE (B.ID, -1) as ID , COALESCE(B.account, '-1') as account, COALESCE (B.side,true) as side
        ,  COALESCE (B.oaccount, '-1') as oaccount,  COALESCE( B.amount,0) as Amount
        , COALESCE(B.duedate,CURRENT_TIMESTAMP) AS duedate, COALESCE (B.text,'TEXT') AS text
        , COALESCE (B.currency, 'EUR') as currency
        FROM master_compta A LEFT  JOIN  details_compta B ON  B.transid = A.id ORDER BY id  ASC""".query

    def delete(id: String): Update0 = sql"""DELETE FROM master_compta WHERE ID = $id.toLong """.update

    override def update(models: List[FinancialsTransaction]): List[Update0] = {
      println("UPDATING", models.map(_.id));
      models.map(m => {
        println("UPDATING", m);
        FinancialsTransactionDetails.update(m.lines);
        update(m)
      })
    }

    override def update(model: FinancialsTransaction): Update0 = sql"""Update master_compta
    set OID=${model.oid}, COSTCENTER=${model.costcenter}, ACCOUNT=${model.account}, TRANSDATE=${model.transdate}
     , HEADERTEXT=${model.text}, FILE_CONTENT=${model.file_content}, TYPE_JOURNAL=${model.typeJournal}
    ,  PERIOD=${model.period}
     where id =${model.tid} AND POSTED=false""".update
  }
  object Journal extends Repository[Journal, Journal] {
    def create(model: Journal): Update0 =
      sql"""INSERT INTO journal (ID,TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIOD, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, modelid, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT ) VALUES
          (SELECT nextval('journal_id_seq'), ${model.transid}, ${model.oid}, ${model.account}, ${model.oaccount}
          , ${model.transdate}, ${model.enterdate}, ${model.postingdate}, ${model.period}, ${model.amount}
          , ${model.side}, ${model.company}, ${model.currency}, ${model.text}, ${model.month}, ${model.year}
          , ${model.modelid}, ${model.file_content}, ${model.typeJournal},${model.idebit}, ${model.debit}
          , ${model.icredit}, ${model.credit} )""".update

    def getBy(id: String): Query0[Journal] = sql"""
     SELECT  ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
     FROM journal
     WHERE id = $id.toLong ORDER BY  id ASC """.query

    def findSome(model: String*): Query0[Journal] = {
      val acc: String = model(0)
      val fromPeriod: Int = model(1).toInt
      val toPeriod: Int = model(2).toInt
      sql"""SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
        , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
        FROM journal WHERE ACCOUNT = $acc AND PERIOD BETWEEN $fromPeriod AND $toPeriod  ORDER BY  id ASC """.query
    }

    def getByModelId(modelid: Int): Query0[Journal] =
      sql""" SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
    FROM journal  Where modelid = $modelid ORDER BY  ID ASC """.query

    def list: Query0[Journal] =
      sql"""SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIOD, AMOUNT,IDEBIT, DEBIT
     , ICREDIT, CREDIT, CURRENCY, SIDE, TEXT, MONTH, YEAR, COMPANY, JOURNAL_TYPE, FILE_CONTENT, MODELID
     FROM journal ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM dual  """.update

    def update(model: Journal): Update0 = sql"""Update dual set column1=true""".update
  }
  object BankStatement extends Repository[BankStatement, BankStatement] {

    def create(item: BankStatement): Update0 =
      sql"""INSERT INTO bankstatement (ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid) VALUES
     (SELECT nextval('bankstatement_id_seq'), ${item.id}, ${item.depositor}, ${item.postingdate}, ${item.valuedate}
    , {item.postingtext}, {item.purpose},${item.beneficiary},  ${item.accountno},{item.bankCode}, {item.amount}
    , {item.currency}, {item.info}, {item.company}, {item.companyIban}, {item.posted}, {item.modelid} """.update

    def getBy(id: String): Query0[BankStatement] = sql"""SELECT ID, DEPOSITOR, POSTINGDATE,
         VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY
         , INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement WHERE id = $id.toLong ORDER BY  id ASC
     """.query

    def getByModelId(modelid: Int): Query0[BankStatement] = sql"""
        SELECT ID, DEPOSITOR, POSTINGDATE, VALUEDATE,POSTINGTEXT, PURPOSE,BENEFICIARY,
            ACCOUNTNO, BANKCODE, AMOUNT, CURRENCY, INFO,COMPANY,COMPANYIBAN, POSTED, modelid
        FROM bankstatement  WHERE modelid = $modelid ORDER BY  id ASC
         """.query

    def list: Query0[BankStatement] = sql"""
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
      sql"""Update bankstatement set POSTINGTEXT=${item.postingtext} , PURPOSE=${item.purpose}
    ,  ACCOUNTNO= ${item.accountno}, BANKCODE=${item.bankCode}, INFO=${item.info}
    ,  COMPANYIBAN=${item.companyIban} where id =${item.id}""".update

  }
  object Vat extends Repository[Vat, Vat] {

    def create(item: Vat): Update0 =
      sql"""INSERT INTO vat (ID, NAME, DESCRIPTION, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT, COMPANY, modelid)
             VALUES (${item.id}, ${item.name}, ${item.description}, ${item.percent}
            , ${item.inputVatAccount}, ${item.outputVatAccount}, ${item.company}, ${item.modelid} )""".update

    def getBy(id: String): Query0[Vat] = sql"""
     SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
     FROM vat WHERE id = $id ORDER BY  id ASC """.query

    def getByModelId(modelid: Int): Query0[Vat] = sql"""
        SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
      , enter_date, modified_date, posting_date, company, modelid
        FROM vat  WHERE modelid = $modelid ORDER BY  id ASC """.query

    def findSome(model: String*): Query0[Vat] = sql"""
        SELECT id, name, description, PERCENT, INPUTVATACCOUNT, OUTPUTVATACCOUNT
        , enter_date, modified_date, posting_date, company, modelid
        FROM vat   ORDER BY  id ASC""".query

    def list: Query0[Vat] = sql"""
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
