package com.kabasoft.iws.shared

import java.util.Date

import boopickle.Default._

import com.kabasoft.iws.shared.common.Amount

import util.{ Try, Success, Failure }
import scalaz._
import Scalaz._

object common {
  type Amount = scala.math.BigDecimal
  def today = new Date()
}
sealed trait IWS {
  def id:String
  def modelId:Int
}

trait ContainerT [+A<:IWS,-B<:IWS] {
  def updated(newItem: B): ContainerT [A,B]
  def remove (item: B): ContainerT  [A,B]
  def size = items.size
  def items : Seq[A]
  def add(newItem: B): ContainerT [A,B]
}

case class Data  (items: Seq[IWS]) extends ContainerT [IWS,IWS]{
  override def updated(newItem: IWS) = {
    items.indexWhere((_.id == newItem.id)) match {
      case -1 =>
        Data(items :+ newItem)
      case index =>
        Data(items.updated(index, newItem))
    }
  }
  override def add(newItem: IWS)= Data(items :+ newItem)
  override def remove (item: IWS) = Data(items.filterNot(_.id==item.id))
}

sealed trait  Masterfile extends IWS {
  def name:String
  def description:String
}
sealed trait Trans extends IWS { def tid:Long}
sealed trait Transaction [L] extends Trans {
    def id = tid.toString
    def tid:Long
    def oid:Long
    def modelId:Int
    def store:Option[String]
    def account:Option[String]
    def lines:Option[List[L]]
  }
sealed trait LineTransaction extends IWS {
  def id = tid.toString
  def tid: Long
  def transid: Long
  def modelId: Int
  def item: Option[String]
  def unit: Option[String]
  def price: Amount
  def duedate: Option[Date]
  def text: String
 }
sealed trait  LineInventoryTransaction extends LineTransaction {
   def quantity: Amount
   }

sealed trait TodoPriority

case object TodoLow extends TodoPriority
case object TodoNormal extends TodoPriority
case object TodoHigh extends TodoPriority
case class Balance( amount: Amount = 0) extends IWS {
  def id =""
  def modelId=0}
case class TodoItem(id: String ="0", timeStamp: Int =0, content: String ="", modelId:Int =4711, priority: TodoPriority =TodoLow, completed: Boolean=false, name:String ="XX",description:String ="kk") extends IWS with Masterfile
object TodoPriority {
  implicit val todoPriorityPickler: Pickler[TodoPriority] = generatePickler[TodoPriority]
}
object  TodoItem_{ def unapply (in:TodoItem) =Some(in.id, in.timeStamp, in.content,in.modelId, in.description, in.priority,in.completed, in.name,in.description)}
case class CostCenter(id:String ="0",  name:String ="", modelId:Int = 6, description:String ="") extends IWS with Masterfile
case class Account (id: String ="", name: String  ="", modelId:Int = 9,description:String  ="",
                    dateOfOpen: Option[Date] = Some(new Date()), dateOfClose: Option[Date] = Some(new Date()), balance: Balance = Balance()) extends Masterfile

case class Article(id:String ="", name:String ="", modelId:Int = 7, description:String  ="", price:Amount = 0, qtty_id:String ="Stk") extends IWS with Masterfile
case class QuantityUnit(id:String ="0",name:String ="", modelId:Int =4 ,description:String ="") extends IWS with Masterfile
case class ArticleGroup(id:String ="", name:String ="", modelId:Int = 8, description:String ="") extends IWS with Masterfile
abstract class BusinessPartner(id: String ="", name: String ="", modelId:Int, street: String ="", city: String ="", state: String ="", zip: String ="") extends IWS
case class Supplier(id: String ="", name: String ="" , modelId:Int = 1, street: String ="", city: String ="", state: String ="", zip: String ="") extends
BusinessPartner (id: String, name: String, modelId:Int, street: String, city: String, state: String , zip: String )
case class Store(id: String ="", name: String ="",  modelId:Int = 2, street: String ="", city: String ="", state: String ="", zip: String ="") extends
BusinessPartner (id: String, name: String ,modelId:Int, street: String, city: String, state: String , zip: String )
case class Customer(id: String ="", name: String ="", modelId:Int = 3, street: String ="", city: String ="", state: String ="", zip: String ="") extends
BusinessPartner (id: String, name: String , modelId:Int, street: String, city: String, state: String , zip: String )


case class Vat(id:String ="",name:String ="", modelId:Int =5 ,description:String ="", percent:Amount =0) extends Masterfile
case class LinePurchaseOrder  (tid:Long, transid:Long, modelId:Int = 102,item:Option[String] = None, unit:Option[String] = None, price: Amount = 0,
                                quantity:Amount = 0,vat:Option[String] = None, duedate:Option[Date] = Some(new Date()),text:String ="") extends LineInventoryTransaction
case class PurchaseOrder [LinePurchaseOrder] (tid:Long =0,oid:Long =0, modelId:Int = 101,store:Option[String]=None, account:Option[String]= None,
                                              lines:Option[List[LinePurchaseOrder]]=None) extends Transaction [LinePurchaseOrder]
object  Supplier_{ def unapply (in:Supplier) =Some(in.id,in.name,in.modelId, in.street,in.city,in.state,in.zip)}
object  Customer_{ def unapply (in:Customer) =Some(in.id,in.name,in.modelId, in.street,in.city,in.state,in.zip)}
object  Store_{ def unapply (in:Store) =Some(in.id,in.name, in.modelId, in.street,in.city,in.state,in.zip)}
object  Vat_{ def unapply (in:Vat) =Some(in.id,in.name, in.modelId, in.description,in.percent)}
object  CostCenter_{ def unapply (in:CostCenter) =Some(in.id,in.name,in.description)}
object  QuantityUnit_{ def unapply (in:QuantityUnit) =Some(in.id,in.name,in.modelId, in.description)}
object  Account_{ def unapply (in:Account) =Some(in.id,in.name,in.modelId, in.description,in.dateOfOpen,in.dateOfClose,in.balance)}
object  Article_{ def unapply (in:Article) =Some(in.id,in.name, in.modelId, in.description, in.price, in.qtty_id)}
object  ArticleGroup_{ def unapply (in:ArticleGroup) =Some(in.id,in.name,in.modelId, in.description)}
object  LinePurchaseOrder_{ def unapply (in:LinePurchaseOrder) = Some(in.tid,in.transid, in.modelId, in.item, in.unit, in.price, in.quantity, in.vat, in.duedate, in.text)}
object  PurchaseOrder_{ def unapply (in:PurchaseOrder[LinePurchaseOrder]) = Some(in.tid,in.oid, in.modelId, in.store, in.account, in.lines)}


object Account {

  private def validateAccountNo(id: String,modelId:Int) =
    if (id.isEmpty || id.size < 5) s"Account No has to be at least 5 characters long: found $id".failureNel[String]
    else id.successNel[String]

  private def validateOpenCloseDate(od: Date, cd: Option[Date]) = cd.map { c =>
    if (c before od) s"Close date [$c] cannot be earlier than open date [$od]".failureNel[(Option[Date], Option[Date])]
    else (od.some, cd).successNel[String]
  }.getOrElse {
    (od.some, cd).successNel[String]
  }

  private def validateRate(rate: BigDecimal) =
    if (rate <= BigDecimal(0)) s"Interest rate $rate must be > 0".failureNel[BigDecimal]
    else rate.successNel[String]

  def checkingAccount(id: String, name: String, modelId:Int, description: String, openDate: Option[Date], closeDate: Option[Date],
                      balance: Balance): \/[NonEmptyList[String], Account] = {

    val od = openDate.getOrElse(new Date())

    (
      validateAccountNo(id,modelId) |@|
        validateOpenCloseDate(openDate.getOrElse(new Date()), closeDate)
      ) { (n, d) =>
      Account(n, name, modelId,description, d._1, d._2, balance)
    }.disjunction
  }


  private def validateAccountAlreadyClosed(a: Account) = {
    if (a.dateOfClose isDefined) s"Account ${a.id} is already closed".failureNel[Account]
    else a.successNel[String]
  }

  private def validateCloseDate(a: Account, cd: Date) = {
    if (cd before a.dateOfOpen.get) s"Close date [$cd] cannot be earlier than open date [${a.dateOfOpen.get}]".failureNel[Date]
    else cd.successNel[String]
  }

  def close(a: Account, closeDate: Date): \/[NonEmptyList[String], Account] = {
    (validateAccountAlreadyClosed(a) |@| validateCloseDate(a, closeDate)) { (acc, d) =>
      acc match {
        case c: Account => c.copy(dateOfClose = Some(closeDate))
        //case s: SavingsAccount  => s.copy(dateOfClose = Some(closeDate))
      }
    }.disjunction
  }

  private def checkBalance(a: Account, amount: Amount) = {
    if (amount < 0 && a.balance.amount < -amount) s"Insufficient amount in ${a.id} to debit".failureNel[Account]
    else a.successNel[String]
  }

  def updateBalance(a: Account, amount: Amount): \/[NonEmptyList[String], Account] = {
    (validateAccountAlreadyClosed(a) |@| checkBalance(a, amount)) { (_, _) =>
      a match {
        case c: Account => c.copy(balance = Balance(c.balance.amount + amount))
        // case s: SavingsAccount  => s.copy(balance = Balance(s.balance.amount + amount))
      }
    }.disjunction
  }
}

object Model {

  import common._

  import boopickle.Default._


  implicit val bigDecimalPickler = transformPickler[scala.math.BigDecimal,String](b=> String.valueOf(b.doubleValue()),
                                                         t =>  scala.math.BigDecimal(t))
  implicit val balancePickler: Pickler[Balance] = generatePickler[Balance]
  implicit val datePickler = transformPickler[java.util.Date,Long](_.getTime,t => new java.util.Date(t))
  implicit val accountPickler: Pickler[Account] = generatePickler[Account]
  implicit val articlePickler: Pickler[Article] = generatePickler[Article]
  implicit val supplierPickler: Pickler[Supplier] = generatePickler[Supplier]
  implicit val costCenterUnitPickler: Pickler[CostCenter] = generatePickler[CostCenter]
  implicit val quantityUnitPickler: Pickler[QuantityUnit] = generatePickler[QuantityUnit]
  implicit val purchaseOrderickler: Pickler[PurchaseOrder[LinePurchaseOrder]] = generatePickler[PurchaseOrder[LinePurchaseOrder]]
  implicit val pickler = compositePickler[IWS]
  pickler.addConcreteType[Masterfile]
  pickler.addConcreteType[TodoItem]
  pickler.addConcreteType[CostCenter]
  pickler.addConcreteType[Balance]
  pickler.addConcreteType[Account]
  pickler.addConcreteType[Article]
  pickler.addConcreteType[Supplier]
  pickler.addConcreteType[Customer]
  pickler.addConcreteType[QuantityUnit]
  pickler.addConcreteType[ArticleGroup]
  pickler.addConcreteType[LinePurchaseOrder]
  pickler.addConcreteType[PurchaseOrder[LinePurchaseOrder]]
  pickler.addConcreteType[Vat]
  pickler.addConcreteType[Store]



 val accounts=List(
   Account("1000", "Kasse", 9, "Kasse", today.some, today.some),
   Account("2000", "Bank", 9, "Bank",  today.some, today.some),
   Account("3000", "Forderung", 9, "Forderung",today.some, today.some)
 )

  val quantityUnits=List(
    QuantityUnit("KG","Kilogramm", 4, "Kilogram"),
    QuantityUnit("Ltr","Liter", 4, "Liter"),
    QuantityUnit("Krt","Karton", 4, "Karton"),
    QuantityUnit("Stk","Stueck", 4, "Stueck")
  )
  val articles=List(
    Article("001","Masterfile", 7, "Financials Application for Enterprise",BigDecimal(5000.0),"Stk"),
    Article("002","Inventory", 7, "Financials Application for Enterprise",BigDecimal(5000.0),"Stk"),
    Article("003","Purchasing",7,  "Financials Application for Enterprise",BigDecimal(5000.0),"Stk"),
    Article("004","CRM", 7, "Financials Application for Enterprise",BigDecimal(5000.0),"Stk"),
    Article("005","Financials",7, "Financials Application for Enterprise",BigDecimal(5000.0),"Stk"),
    Article("006","Analytics",7, "Enterprise Analytics & Decision support for Management",BigDecimal(50000.0),"Stk")


  )

  val suppliers = List(
    Supplier("101", "Acme, Inc.",  1,    "99 Market Street", "Groundsville", "CA", "95199"),
    Supplier( "499", "Superior Coffee", 1, "1 Party Place",    "Mendocino",    "CA", "95460"),
    Supplier("150", "The High Ground", 1, "100 Coffee Lane",  "Meadows",      "CA", "93966")
  )
  val customers = List(
    Customer("501", "Chemec GmbH.",3,      "99 Market Street", "Groundsville", "CA", "95199"),
    Customer( "555", "Studentenwerk Bielefeld  AoR",3, "1 Party Place",    "Mendocino",    "CA", "95460"),
    Customer("560", "Gulp GmbH", 3, "100 Coffee Lane",   "Meadows",      "CA", "93966")
  )

  val constCenters=List(
    CostCenter("100","Vertrieb", 6,  "Sales"),
    CostCenter("200","Einkauf", 6, "Purchasing"),
    CostCenter("300","Produktion", 6, "Production"),
    CostCenter("400","Lager", 6, "Store"),
    CostCenter("500","Verwaltung", 6, "Accounting"),
    CostCenter("600","Geschaeftsleitung", 6, "Management")
  )

  val articleGroups=List(
    ArticleGroup("100","Getraenke", 8,  "Getraenke"),
    ArticleGroup("200","Auto", 8, "Auto"),
    ArticleGroup("300","Software", 8, "Software"),
    ArticleGroup("400","Hardware", 8, "Hardware"),
    ArticleGroup("500","Electronik", 8, "Electronik"),
    ArticleGroup("600","Service", 8, "Service")
  )


}
