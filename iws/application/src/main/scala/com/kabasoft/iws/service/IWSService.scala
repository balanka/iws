package com.kabasoft.iws.service

import com.kabasoft.iws.domain._
import scala.language.higherKinds

trait Service[F[_], A] {

  def create(item: A): F[Int]
  def delete(item: String): F[Int]
  def list(from: Int, until: Int): F[List[A]]
  def getBy(id: String): F[Option[A]]
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[A]]
  def findSome(model: String*): F[List[A]]
  def update(model: A): F[Int]
}

class MasterfileService[F[_]](repository: Repository[F, Masterfile]) extends Service[F, Masterfile] {
  def create(item: Masterfile): F[Int] = { println("item<<<<<<<<<<<", item); repository.create(item) }
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Masterfile]] = repository.list(from, until)
  def getBy(id: String): F[Option[Masterfile]] = repository.getBy(id)
  def findSome(model: String*): F[List[Masterfile]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Masterfile]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Masterfile): F[Int] = repository.update(model)
}
class CostCenterService[F[_]](repository: Repository[F, CostCenter]) extends Service[F, CostCenter] {
  def create(item: CostCenter): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[CostCenter]] = repository.list(from, until)
  def getBy(id: String): F[Option[CostCenter]] = repository.getBy(id)
  def findSome(model: String*): F[List[CostCenter]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[CostCenter]] =
    repository.getByModelId(modelid, from, until)
  def update(model: CostCenter): F[Int] = repository.update(model)
}

class PeriodicAccountBalanceService[F[_]](repository: Repository[F, PeriodicAccountBalance])
    extends Service[F, PeriodicAccountBalance] {
  def create(item: PeriodicAccountBalance): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[PeriodicAccountBalance]] = repository.list(from, until)
  def getBy(id: String): F[Option[PeriodicAccountBalance]] = repository.getBy(id)
  def findSome(model: String*): F[List[PeriodicAccountBalance]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[PeriodicAccountBalance]] =
    repository.getByModelId(modelid, from, until)
  def update(model: PeriodicAccountBalance): F[Int] = repository.update(model)
}
class CustomerService[F[_]](repository: Repository[F, Customer]) extends Service[F, Customer] {
  def create(item: Customer): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Customer]] = repository.list(from, until)
  def getBy(id: String): F[Option[Customer]] = repository.getBy(id)
  def findSome(model: String*): F[List[Customer]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Customer]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Customer): F[Int] = repository.update(model)
}
class SupplierService[F[_]](repository: Repository[F, Supplier]) extends Service[F, Supplier] {
  def create(item: Supplier): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Supplier]] = repository.list(from, until)
  def getBy(id: String): F[Option[Supplier]] = repository.getBy(id)
  def findSome(model: String*): F[List[Supplier]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Supplier]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Supplier): F[Int] = repository.update(model)
}
class AccountService[F[_]](repository: Repository[F, Account]) extends Service[F, Account] {
  def create(item: Account): F[Int] = { println("item>>>>>>>>>>>>>", item); repository.create(item) }
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Account]] = repository.list(from, until)
  def getBy(id: String): F[Option[Account]] = repository.getBy(id)
  def findSome(model: String*): F[List[Account]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Account]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Account): F[Int] = repository.update(model)
}
class ArticleService[F[_]](repository: Repository[F, Article]) extends Service[F, Article] {
  def create(item: Article): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Article]] = repository.list(from, until)
  def getBy(id: String): F[Option[Article]] = repository.getBy(id)
  def findSome(model: String*): F[List[Article]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Article]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Article): F[Int] = repository.update(model)
}
class FinancialsTransactionService[F[_]](repository: Repository[F, FinancialsTransaction])
    extends Service[F, FinancialsTransaction] {
  def create(item: FinancialsTransaction): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[FinancialsTransaction]] = repository.list(from, until)
  def getBy(id: String): F[Option[FinancialsTransaction]] = repository.getBy(id)
  def findSome(model: String*): F[List[FinancialsTransaction]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransaction]] =
    repository.getByModelId(modelid, from, until)
  def update(model: FinancialsTransaction): F[Int] = repository.update(model)
}
class FinancialsTransactionDetailsService[F[_]](repository: Repository[F, FinancialsTransactionDetails])
    extends Service[F, FinancialsTransactionDetails] {
  def create(item: FinancialsTransactionDetails): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[FinancialsTransactionDetails]] = repository.list(from, until)
  def getBy(id: String): F[Option[FinancialsTransactionDetails]] = repository.getBy(id)
  def findSome(model: String*): F[List[FinancialsTransactionDetails]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[FinancialsTransactionDetails]] =
    repository.getByModelId(modelid, from, until)
  def update(model: FinancialsTransactionDetails): F[Int] = repository.update(model)
}
class RoutesService[F[_]](repository: Repository[F, Routes]) extends Service[F, Routes] {
  def create(item: Routes): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Routes]] = repository.list(from, until)
  def getBy(id: String): F[Option[Routes]] = repository.getBy(id)
  def findSome(model: String*): F[List[Routes]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Routes]] = repository.getByModelId(modelid, from, until)
  def update(model: Routes): F[Int] = repository.update(model)
}
class JournalService[F[_]](repository: Repository[F, Journal]) extends Service[F, Journal] {
  def create(item: Journal): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Journal]] = repository.list(from, until)
  def getBy(id: String): F[Option[Journal]] = repository.getBy(id)
  def findSome(model: String*): F[List[Journal]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Journal]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Journal): F[Int] = repository.update(model)
}

class BankStatementService[F[_]](repository: Repository[F, BankStatement]) extends Service[F, BankStatement] {
  def create(item: BankStatement): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[BankStatement]] = repository.list(from, until)
  def getBy(id: String): F[Option[BankStatement]] = repository.getBy(id)
  def findSome(model: String*): F[List[BankStatement]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[BankStatement]] =
    repository.getByModelId(modelid, from, until)
  def update(model: BankStatement): F[Int] = repository.update(model)
}
class VatService[F[_]](repository: Repository[F, Vat]) extends Service[F, Vat] {
  def create(item: Vat): F[Int] = repository.create(item)
  def delete(id: String): F[Int] = repository.delete(id)
  def list(from: Int, until: Int): F[List[Vat]] = repository.list(from, until)
  def getBy(id: String): F[Option[Vat]] = repository.getBy(id)
  def findSome(model: String*): F[List[Vat]] = repository.findSome(model: _*)
  def getByModelId(modelid: Int, from: Int, until: Int): F[List[Vat]] =
    repository.getByModelId(modelid, from, until)
  def update(model: Vat): F[Int] = repository.update(model)
}
object MasterfileService {
  def apply[F[_]](repository: Repository[F, Masterfile]): MasterfileService[F] = new MasterfileService[F](repository)
}
object CostCenterService {
  def apply[F[_]](repository: Repository[F, CostCenter]): CostCenterService[F] = new CostCenterService[F](repository)
}
object PeriodicAccountBalanceService {
  def apply[F[_]](repository: Repository[F, PeriodicAccountBalance]): PeriodicAccountBalanceService[F] =
    new PeriodicAccountBalanceService[F](repository)
}
object CustomerService {
  def apply[F[_]](repository: Repository[F, Customer]): CustomerService[F] = new CustomerService[F](repository)
}
object SupplierService {
  def apply[F[_]](repository: Repository[F, Supplier]): SupplierService[F] = new SupplierService[F](repository)
}
object RoutesService {
  def apply[F[_]](repository: Repository[F, Routes]): RoutesService[F] = new RoutesService[F](repository)
}

object AccountService {
  def apply[F[_]](repository: Repository[F, Account]): AccountService[F] = new AccountService[F](repository)
}

object ArticleService {
  def apply[F[_]](repository: Repository[F, Article]): ArticleService[F] = new ArticleService[F](repository)
}
object FinancialsTransactionService {
  def apply[F[_]](repository: Repository[F, FinancialsTransaction]): FinancialsTransactionService[F] =
    new FinancialsTransactionService[F](repository)
}
object FinancialsTransactionDetailsService {
  def apply[F[_]](repository: Repository[F, FinancialsTransactionDetails]): FinancialsTransactionDetailsService[F] =
    new FinancialsTransactionDetailsService[F](repository)
}

object JournalService {
  def apply[F[_]](repository: Repository[F, Journal]): JournalService[F] =
    new JournalService[F](repository)
}
object BankStatementService {
  def apply[F[_]](repository: Repository[F, BankStatement]): BankStatementService[F] =
    new BankStatementService[F](repository)
}
object VatService {
  def apply[F[_]](repository: Repository[F, Vat]): VatService[F] =
    new VatService[F](repository)
}
