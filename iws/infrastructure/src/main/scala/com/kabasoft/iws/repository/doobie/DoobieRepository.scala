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
    , ${item.modelId},  ${item.parent},  ${item.price},  ${item.stocked})""".update

    def select(id: String): Query0[Article] = sql"""
     SELECT id, name, description, modelId, price, parent, stocked
     FROM article
     WHERE id = $id ORDER BY  id ASC
    """.query

    def findByModelId(modelId: Int): Query0[Article] = sql"""
     SELECT id, name, description, modelId, price, parent, stocked
     FROM article
     WHERE modelId = $modelId ORDER BY  id ASC
    """.query

    def all: Query0[Article] = sql"""
     SELECT id, name, description, modelId, parent,  price, stocked
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
  object Pet {
    def create(item: Pet): Update0 =
      sql"""INSERT INTO pets (ID, NAME) VALUES(${item.id}, ${item.name})""".update

    def all: Query0[Pet] = sql"""
     SELECT id, name
     FROM pets
     ORDER BY id ASC
  """.query

    def select(id: String): Query0[Pet] = sql"""
     SELECT id, name
     FROM pets
     WHERE id = $id ORDER BY  id ASC
      """.query

    def findByModelId(modelId: Int): Query0[Pet] = sql"""
      SELECT id, name FROM pets WHERE modelId = $modelId  ORDER BY  id ASC """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM pets WHERE ID = $id""".update

    def update(model: Pet): Update0 = sql"""Update pets set id = ${model.id}, name =${model.name}
         where id =${model.id}""".update
  }

  object Routes {

    def create(item: Routes): Update0 =
      sql"""INSERT INTO Routes (ID, NAME, DESCRIPTION, MODELID, PARENT) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelId},  ${item.component} )""".update

    def select(id: String): Query0[Routes] = sql"""
     SELECT id, name, description, modelId, parent as component
     FROM Routes
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[Routes] = sql"""
        SELECT id, name, description, modelId, parent as component
        FROM Routes  WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[Routes] = sql"""
     SELECT id, name,description, modelId, parent as component
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
      sql"""INSERT INTO masterfiles (ID, NAME, DESCRIPTION, MODELID, PARENT) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelId},  ${item.parent} )""".update

    def select(id: String): Query0[Masterfile] = sql"""
     SELECT id, name, description, modelId, parent
     FROM masterfiles
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[Masterfile] = sql"""
        SELECT id, name, description, modelId, parent
        FROM masterfiles  WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[Masterfile] = sql"""
     SELECT id, name,description, modelId, parent
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
      sql"""INSERT INTO costcenter (ID, NAME, DESCRIPTION, MODELID, ACCOUNT) VALUES
     (${item.id.value}, ${item.name}, ${item.description}
    , ${item.modelId},  ${item.account},  )""".update

    def select(id: String): Query0[CostCenter] = sql"""
     SELECT id, name, description, modelId, ACCOUNT, enter_date, modified_date, posting_date
     FROM costcenter
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[CostCenter] = sql"""
        SELECT id, name, description, modelId, ACCOUNT, enter_date, modified_date, posting_date
        FROM costcenter  WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[CostCenter] = sql"""
     SELECT id, name,description, modelId, ACCOUNT, enter_date, modified_date, posting_date
     FROM costcenter
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM costcenter WHERE ID = $id""".update

    def update(model: CostCenter): Update0 = sql"""Update costcenter set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}  where id =${model.id}""".update
  }
  object Account {
    def create(item: Account): Update0 =
      sql"""INSERT INTO account (ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             MODELID, ACCOUNT, isDebit, balancesheet) VALUES  (${item.id}, ${item.name}, ${item.description}
             , ${item.enterdate}, ${item.changedate}, ${item.modelid},${item.account},${item.isDebit}
             ,${item.balancesheet})""".update

    def select(id: String): Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             MODELID, ACCOUNT, isDebit, balancesheet
     FROM account
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[Account] = sql"""
        SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             MODELID, ACCOUNT, isDebit, balancesheet
        FROM account WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[Account] = sql"""
     SELECT ID, NAME, DESCRIPTION, posting_date, modified_date, enter_date, company,
             MODELID, ACCOUNT, isDebit, balancesheet
     FROM account
     ORDER BY id ASC
     """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM account WHERE ID = $id""".update

    def update(model: Account): Update0 = sql"""Update accounts set id = ${model.id}, name =${model.name}
        ,  description=${model.description}, posting_date ={model.posting_date}, modified_date ={model.modified_date}
         , enter_date = {model.enter_date}, company ={model.company}, account ={model.account}
         , isDebit ={model.isDebit}, balancesheet ={model.balancesheet} where id =${model.id} """.update

  }

  object PeriodicAccountBalance {
    def create(item: PeriodicAccountBalance): Update0 =
      sql"""INSERT INTO periodic_account_balance (ID, ACCOUNT, PERIODE, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, MODELID) VALUES (
              ${item.id}, ${item.account}, ${item.periode}, ${item.idebit}, ${item.icredit}, ${item.debit}, ${item.credit}
             , ${item.company}, ${item.currency}, ${item.modelid} )""".update

    def select(id: String): Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIODE, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, MODELID
     FROM periodic_account_balance
     WHERE id = $id ORDER BY  PERIODE ASC
     """.query

    def findByModelId(modelId: Int): Query0[PeriodicAccountBalance] = sql"""
        SELECT ID, ACCOUNT, PERIODE, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, MODELID
        FROM periodic_account_balance WHERE modelId = $modelId ORDER BY  PERIODE ASC
         """.query

    def all: Query0[PeriodicAccountBalance] = sql"""
     SELECT ID, ACCOUNT, PERIODE, IDEBIT, ICREDIT, DEBIT, CREDIT,  company, CURRENCY, MODELID
     FROM periodic_account_balance
     ORDER BY PERIODE ASC """.query

    def delete(id: String): Update0 =
      sql"""DELETE FROM periodic_account_balance WHERE ID = $id""".update

    def update(model: PeriodicAccountBalance): Update0 = sql"""Update periodic_account_balance
         set IDEBIT=${model.idebit}, ICREDIT=${model.icredit}, DEBIT=${model.debit}, CREDIT=${model.credit}
         , COMPANY=${model.company}, CURRENCY ={model.company} where id =${model.id} """.update
  }

  object Customer {

    def create(item: Customer): Update0 =
      sql"""INSERT INTO customer (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP,
            PHONE, EMAIL, ACCOUNT, IBAN, VATCODE, REVENUE_ACCOUNT, COMPANY, MODELID ) VALUES
     (${item.id.value}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
     , ${item.state}, ${item.phone},  ${item.email}, ${item.account}, ${item.iban}
     , ${item.vatcode}, ${item.oaccount}, ${item.company} , ${item.modelId})""".update

    def select(id: String): Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, REVENUE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
     FROM customer
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[Customer] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, REVENUE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
        FROM customer  WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[Customer] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, REVENUE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
     FROM customer
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM customer WHERE ID = $id""".update

    def update(model: Customer): Update0 = sql"""Update customer set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}, street=${model.street}, city=${model.city}
         , state = ${model.state},phone= ${model.phone},  email= ${model.email}, iban =${model.iban}, vatcode=${model.vatcode}
        ,  account= ${model.account}, revenue_account=${model.oaccount}, company=${model.company} , ${model.modelId}
          where id =${model.id}""".update
  }

  object Supplier {

    def create(item: Supplier): Update0 =
      sql"""INSERT INTO supplier (ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP,
            PHONE, EMAIL, ACCOUNT, IBAN, VATCODE, CHARGE_ACCOUNT, COMPANY, MODELID ) VALUES
     (${item.id.value}, ${item.name}, ${item.description} , ${item.street}, ${item.city}
     , ${item.state}, ${item.phone},  ${item.email}, ${item.account}, ${item.iban}
     , ${item.vatcode}, ${item.oaccount}, ${item.company} , ${item.modelId})""".update

    def select(id: String): Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, CHARGE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
     FROM supplier
     WHERE id = $id ORDER BY  id ASC
     """.query

    def findByModelId(modelId: Int): Query0[Supplier] = sql"""
        SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, CHARGE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
        FROM supplier  WHERE modelId = $modelId ORDER BY  id ASC
         """.query

    def all: Query0[Supplier] = sql"""
     SELECT ID, NAME, DESCRIPTION, STREET, CITY, STATE, ZIP, PHONE, EMAIL, ACCOUNT
             , IBAN, VATCODE, CHARGE_ACCOUNT, COMPANY, MODELID, enter_date, modified_date, posting_date
     FROM supplier
     ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM customer WHERE ID = $id""".update

    def update(model: Supplier): Update0 = sql"""Update supplier set id = ${model.id}, name =${model.name},
         description=${model.description}, account=${model.account}, street=${model.street}, city=${model.city}
         , state = ${model.state},phone= ${model.phone},  email= ${model.email}, iban =${model.iban}, vatcode=${model.vatcode}
        ,  account= ${model.account}, charge_account=${model.oaccount}, company=${model.company} , ${model.modelId}
          where id =${model.id}""".update
  }
  object FinancialsTransactionDetailsRepo {

    def create(model: FinancialsTransactionDetails): Update0 =
      sql"""INSERT INTO details_compta (ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT,DUEDATE, TEXT, CURRENCY, COMPANY,TERMS) VALUES
     (SELECT nextval('details_compta_Id_seq'), ${model.transid}, ${model.account}, ${model.side}, ${model.oaccount}, ${model.amount}
    , ${model.duedate}, ${model.text},  ${model.currency}, ${model.company} )""".update

    def select(id: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE id = $id ORDER BY  id ASC """.query

    def findSome(id: String): Query0[FinancialsTransactionDetails] = sql"""
     SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
     FROM details_compta WHERE TRANSID = $id.toInt ORDER BY  id ASC """.query

    def findByModelId(modelId: Int): Query0[FinancialsTransactionDetails] = sql"""
        SELECT ID, TRANSID, ACCOUNT, SIDE, OACCOUNT, AMOUNT, DUEDATE, TEXT, CURRENCY, COMPANY,TERMS
        FROM details_compta  WHERE modelId = $modelId ORDER BY  id ASC
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
      sql"""INSERT INTO master_compta (ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIODE, POSTED, MODELID,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT ) VALUES
          (SELECT nextval('master_compta_Id_seq'), ${model.oid}, ${model.costcenter}, ${model.account},${model.transdate},
          ${model.postingdate}, ${model.enterdate}, ${model.periode}, ${model.posted}, ${model.modelId},
         ${model.company}, ${model.text},  ${model.typeJournal},  ${model.file_content} )""".update

    def select(id: String): Query0[FinancialsTransaction] = sql"""
     SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIODE, POSTED, MODELID,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta
     WHERE id = $id.toLong ORDER BY  id ASC """.query[FinancialsTransaction_Type].map(FinancialsTransaction.apply)

    def findSome(id: String): Query0[FinancialsTransaction] = sql"""
     SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIODE, POSTED, MODELID,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta
     WHERE id = $id.toLong ORDER BY  id ASC """.query[FinancialsTransaction_Type].map(FinancialsTransaction.apply)

    def findByModelId(modelId: Int): Query0[FinancialsTransaction_Type2] = sql"""
    SELECT A.ID, A.OID, A.COSTCENTER, A.ACCOUNT, A.TRANSDATE, A.POSTINGDATE, A.ENTERDATE, A.PERIODE, A.POSTED, A.MODELID,
       A.COMPANY, A.HEADERTEXT, A.TYPE_JOURNAL, A.FILE_CONTENT, B.ID, B.account,
       B.side, B.oaccount, B.amount, B.duedate, B.text, B.currency, B.terms
    FROM master_compta A, details_compta B  WHERE A.id = B.transid and A.modelId = $modelId ORDER BY  A.ID ASC """.query

    def all: Query0[FinancialsTransaction_Type] = sql"""
     SELECT ID, OID, COSTCENTER, ACCOUNT, TRANSDATE, POSTINGDATE, ENTERDATE, PERIODE, POSTED, MODELID,
            COMPANY, HEADERTEXT, TYPE_JOURNAL, FILE_CONTENT
     FROM master_compta ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM master_compta WHERE ID = $id.toLong """.update

    def update(model: FinancialsTransaction): Update0 = sql"""Update master_compta
    set OID=${model.oid}, COSTCENTER=${model.costcenter}, ACCOUNT=${model.account}, HEADERTEXT=${model.text},
     TRANSDATE=${model.transdate}, FILE_CONTENT=${model.file_content} TYPE_JOURNAL=${model.typeJournal}
    , COMPANY=${model.company} , MODELID=${model.modelId}, PERIODE=${model.periode}
     where id =${model.id}""".update
  }

  object Journal {
    def create(model: Journal): Update0 =
      sql"""INSERT INTO journal (ID,TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIODE, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, MODELID, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT ) VALUES
          (SELECT nextval('journal_id_seq'), ${model.transid}, ${model.oid}, ${model.account}, ${model.oaccount}
          , ${model.transdate}, ${model.enterdate}, ${model.postingdate}, ${model.periode}, ${model.amount}
          , ${model.side}, ${model.company}, ${model.currency}, ${model.text}, ${model.month}, ${model.year}
          , ${model.modelId}, ${model.file_content}, ${model.typeJournal},${model.idebit}, ${model.debit}
          , ${model.icredit}, ${model.credit} )""".update

    def select(id: String): Query0[Journal] = sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIODE, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, MODELID, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal
     WHERE id = $id.toLong ORDER BY  id ASC """.query

    def findSome(id: String): Query0[Journal] = sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIODE, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, MODELID, FILE_CONTENT, TJOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal
     WHERE id = $id.toLong ORDER BY  id ASC """.query

    def findByModelId(modelId: Int): Query0[Journal] = sql"""
    SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIODE, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, MODELID, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
    FROM journal  Where MODELID = $modelId ORDER BY  ID ASC """.query

    def all: Query0[Journal] = sql"""
     SELECT ID, TRANSID, OID, ACCOUNT, OACCOUNT, TRANSDATE, ENTERDATE, POSTINGDATE, PERIODE, AMOUNT, SIDE
           , COMPANY, CURRENCY, TEXT, MONTH, YEAR, MODELID, FILE_CONTENT, JOURNAL_TYPE, IDEBIT, DEBIT, ICREDIT, CREDIT
     FROM journal ORDER BY id  ASC
  """.query

    def delete(id: String): Update0 = sql"""DELETE FROM dual  """.update

    def update(model: Journal): Update0 = sql"""Update dual set column1=true""".update
  }
  //implicit val petMeta: Meta[PetId] = Meta[Long].timap(PetId.apply)(_.toLong)

  //implicit private val petNameGet: Get[PetName] = Get[String].tmap(name => PetName(name))

  //implicit val masterfileMeta: Meta[MasterfileId] = Meta[String].timap(MasterfileId.apply)(_.value)

  //implicit private val masterfilesNameGet: Get[MasterfileName] = Get[String].tmap(name => MasterfileName(name))
}
//class MyClass extends MyTrait[({ type l[A] = Map[String, A] })#l] λ[A => X[A, Throwable]]
//final case class DoobiePetRepository[F[_]: Bracket[?[_], Throwable]](transactor: Transactor[F])
/*
class Foo[A[_,_],B] {
  type AB[C] = A[B, C]
  def apply(functor: Functor[AB]) = ...
}
 */

final class DoobiePetRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Pet] {

  import SQL._
  //implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  override def list(from: Int, until: Int): F[List[Pet]] =
    paginate(until - from, from)(Pet.all).to[List].transact(transactor)
  //.stream
  //.map(Pets.apply)
  //.transact(transactor)

  def create(pet: Pet): F[Int] = SQL.Pet.create(pet).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Pet.delete(id).run.transact(transactor)
  def update(model: Pet): F[Int] = SQL.Pet.update(model).run.transact(transactor)
  override def getBy(id: String): F[Option[Pet]] = Pet.select(id).option.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Pet]] =
    paginate(until - from, from)(Pet.findByModelId(modelid)).to[List].transact(transactor)

  def findSome(id: String): F[List[Pet]] = list(0, 1000000)
}

object DoobiePetRepository {

  def apply[F[_]: Sync](transactor: Transactor[F]): DoobiePetRepository[F] =
    new DoobiePetRepository[F](transactor)
}

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

  def findSome(id: String): F[List[Routes]] = list(0, 1000000)
}

object DoobieRoutesRepository {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieRoutesRepository[F] =
    new DoobieRoutesRepository[F](transactor)
}

final case class DoobieMasterfileRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Masterfile] {

  import SQL._

  def create(pet: Masterfile): F[Int] = SQL.Masterfile.create(pet).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Masterfile.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(Masterfile.all)
      .to[List]
      .transact(transactor)

  override def getBy(id: String): F[Option[Masterfile]] = Masterfile.select(id).option.transact(transactor)

  def update(model: Masterfile): F[Int] = SQL.Masterfile.update(model).run.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Masterfile]] =
    paginate(until - from, from)(Masterfile.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(id: String): F[List[Masterfile]] = list(0, 1000000)
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

  def findSome(id: String): F[List[CostCenter]] = list(0, 1000000)
}

object DoobieCostCenterRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieCostCenterRepository[F] =
    new DoobieCostCenterRepository[F](transactor)
}

final case class DoobieAccountRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Account] {

  import SQL._

  def create(item: Account): F[Int] = SQL.Account.create(item).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Account.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(Account.all)
      .to[List]
      .transact(transactor)

  def update(model: Account): F[Int] = SQL.Account.update(model).run.transact(transactor)
  override def getBy(id: String): F[Option[Account]] = Account.select(id).option.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Account]] =
    paginate(until - from, from)(Account.findByModelId(modelid))
      .to[List]
      .transact(transactor)

  def findSome(id: String): F[List[Account]] = list(0, 1000000)

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

  def findSome(id: String): F[List[Article]] = list(0, 1000000)
}

object DoobieArticleRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieArticleRepository[F] =
    new DoobieArticleRepository[F](transactor)
}
final case class DoobieCustomerRepository[F[_]: Sync](transactor: Transactor[F]) extends Repository[F, Customer] {
  import SQL._

  def create(customer: Customer): F[Int] = SQL.Customer.create(customer).run.transact(transactor)
  def delete(id: String): F[Int] = SQL.Customer.delete(id).run.transact(transactor)

  override def list(from: Int, until: Int): F[List[Customer]] =
    paginate(until - from, from)(Customer.all).to[List].transact(transactor)

  override def getBy(id: String): F[Option[Customer]] = Customer.select(id).option.transact(transactor)
  def update(model: Customer): F[Int] = SQL.Customer.update(model).run.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Customer]] =
    paginate(until - from, from)(Customer.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  def findSome(id: String): F[List[Customer]] = list(0, 1000000)
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
  def update(model: PeriodicAccountBalance): F[Int] = SQL.PeriodicAccountBalance.update(model).run.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    paginate(until - from, from)(PeriodicAccountBalance.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  def findSome(id: String): F[List[PeriodicAccountBalance]] = list(0, 1000000)
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
  def update(model: Supplier): F[Int] = SQL.Supplier.update(model).run.transact(transactor)
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[Supplier]] =
    //SQL.Supplier.findByModelId(modelid).to[List].transact(transactor)
    paginate(until - from, from)(Supplier.findByModelId(modelid))
    //.map(Supplier.apply)
      .to[List]
      .transact(transactor)
  def findSome(id: String): F[List[Supplier]] = list(0, 1000000)
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
  override def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransactionDetails]] =
    paginate(until - from, from)(FinancialsTransactionDetailsRepo.findByModelId(modelid))
      .to[List]
      .transact(transactor)
  def findSome(id: String): F[List[FinancialsTransactionDetails]] = //list(0, 1000000)
    FinancialsTransactionDetailsRepo.findSome(id).to[List].transact(transactor)
}

object DoobieFinancialsTransactionDetailsRepository {
  def apply[Id[_]: Sync](transactor: Transactor[Id]): DoobieFinancialsTransactionDetailsRepository[Id] =
    new DoobieFinancialsTransactionDetailsRepository[Id](transactor)
  def findSome[Id[_]: Sync](transactor: Transactor[Id], id: String): Id[List[FinancialsTransactionDetails]] =
    FinancialsTransactionDetailsRepo.findSome(id).to[List].transact(transactor)
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

  def f(id: String): F[List[FinancialsTransactionDetails]] =
    // tr.copy(
    // lines =
    FinancialsTransactionDetailsRepo
      .findSome(id)
      .to[List]
      .transact(transactor)
  //.unsafeRunSync
  //)

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

  def findSome(id: String): F[List[FinancialsTransaction]] = list(0, 1000000)

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
  def findSome(id: String): F[List[Journal]] = list(0, 1000000)
}

object DoobieJournalRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): DoobieJournalRepository[F] =
    new DoobieJournalRepository[F](transactor)
}
