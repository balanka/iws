package com.kabasoft.iws.client.services


import autowire._
import diode._
import diode.util._
import diode.data._
import diode.react.ReactConnector
import com.kabasoft.iws.client.logger._
import com.kabasoft.iws.shared.{Store => MStore, _}
import com.kabasoft.iws.shared._
import com.kabasoft.iws.shared.Model._
import boopickle.Default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import com.kabasoft.iws.gui.macros._

case class UpdateMotd(value: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

// The base model of our application
case class RootModel [+A<:IWS,-B<:IWS](store:Pot[Store[A,B]], motd:Pot[String])
case class Store [+A<:IWS,-B<:IWS] (models: Map[Int, Pot[ContainerT[A,B]]]) {
  def updated (newItem: B) = {
      val x= models.get(newItem.modelId).get.map(_.updated(newItem))
      Store (models+ (newItem.modelId -> x))
  }
  def updatedAll(newModels: Map[Int, Pot[ContainerT[B,A]]])  = Store[A,B](models.asInstanceOf[ Map[Int, Pot[ContainerT[A,B]]]]++
    newModels.asInstanceOf[ Map[Int, Pot[ContainerT[A,B]]]])
  def remove(item:B) = {
    val x= models.get(item.modelId).get.map(_.remove(item))
    Store( Map(item.modelId ->x))
  }
 }


 /**
  * Handles actions related to todos
  * @param modelRW Reader/Writer to access the model
  * @tparam M
  */

class IWSHandler[M](modelRW: ModelRW[M, Pot[Store[IWS,IWS]]]) extends ActionHandler(modelRW) {
  import boopickle.Default._
   import scala.collection.mutable.ListBuffer
  implicit val bigDecimalPickler = transformPickler[scala.math.BigDecimal,String](
                                   b=> String.valueOf(b.doubleValue()),
                                   t =>  scala.math.BigDecimal(t))
  implicit val balancePickler: Pickler[Balance] = generatePickler[Balance]
  implicit val articleGroupPickler: Pickler[ArticleGroup] = generatePickler[ArticleGroup]
  implicit val pickler = compositePickler[IWS]
  pickler.addConcreteType[TodoItem]
  pickler.addConcreteType[CostCenter]
  pickler.addConcreteType[QuantityUnit]
  pickler.addConcreteType[Account]
  pickler.addConcreteType[Article]
  pickler.addConcreteType[Balance]
  pickler.addConcreteType[ArticleGroup]
  pickler.addConcreteType[Customer]
  pickler.addConcreteType[Supplier]
  pickler.addConcreteType[MStore]
  pickler.addConcreteType[LinePurchaseOrder]
  pickler.addConcreteType[PurchaseOrder[LinePurchaseOrder]]

  override def handle = {
    case Refresh (item:IWS) =>
      val x=Map(item.modelId ->Ready(Data(Seq(item))))
      updated(Ready(value.get.updatedAll(x)))
    case UpdateAll(todos:Seq[IWS]) =>
      val xx=todos.seq.headOption.get
      log.info("+++++++++>>>>>>>>XXX"+xx)
      val  a=todos.filter(_.modelId==xx.modelId)
      log.info("+++++++++<<<<<<<<<<<"+a)
      val x=Map(xx.modelId ->Ready(Data(a)))
      updated(Ready(value.get.updatedAll(x)))
    case Update(item:IWS) =>
      log.info("+++++++++<<<<<<<<<<< UpdateTodo: "+item)
      updated(Ready(value.get.updated(item)), Effect(AjaxClient[Api].update(item).call().map(UpdateAll[IWS])) )
    case Delete(item:IWS) =>
      updated(Ready(value.get.remove(item)).asInstanceOf[Pot[Store[IWS,IWS]]],
                    Effect(AjaxClient[Api].delete(item).call().map(UpdateAll[IWS])))
  }
}



/**
  * Handles actions related to the Motd
  * @param modelRW Reader/Writer to access the model
  * @tparam M
  */
class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[Api].welcome("User X").call())(identity)
      action.handleWith(this, updateF)(PotAction.handler(Retry(3)))
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel[IWS,IWS]] with ReactConnector[RootModel[IWS,IWS]] {

 /* x.onComplete( {
     case value:List[IWS] => log.info("+++++++++<<<<<<<<<<< xx: "+value)
     case _ =>  log.info("++===========ERRORRRRRR")
  }
  )
  */

// val s:Pot[Store[IWS,IWS]] = Ready(Store(Map.empty[Int, Pot[ContainerT[IWS,IWS]]]))
//  protected var model = RootModel(s, Empty)
//
//  protected val actionHandler = combineHandlers(
//    new IWSHandler(zoomRW(_.store)((m, v) => m.copy(store = v))),
//    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
//  )
//
//  var lx = new ListBuffer[IWS]
//  def p(x:List[IWS])=  {
//    lx++=x
//    log.info("++===========XzzzzzzzzzzzzZZZZZ==========="+lx)
//
//    val store: Pot[Store[IWS, IWS]] = Ready(Store(Map(
//      4 -> Ready(Data(Seq(QuantityUnit("1", "QuantityUnit", 4, "QuantityUnit")))),
//      6 -> Ready(Data(Seq(CostCenter("1", "CostCenter", 6, "CostCenter")))),
//      7 -> Ready(Data(Seq(Article()))),
//      9 -> Ready(Data(Seq(Account()))),
//      //8 -> Ready(Data(lx)),
//      8 -> Ready(Data(Seq(ArticleGroup()))),
//      4711 -> Ready(Data(Seq(TodoItem())))
//    )))
//
//    model = RootModel(store, Empty)
//
//  }

  //AjaxClient[Api].all(Article()).call().map(p)
 // log.info("++===========XzzzzzzzzzzzzZZZZZYYYYYYY==========="+lx)
  protected var model = RootModel( Ready(Store(Map.empty[Int, Pot[ContainerT[IWS,IWS]]])), Empty)
  protected val actionHandler = combineHandlers(
    new IWSHandler(zoomRW(_.store)((m, v) => m.copy(store = v))),
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
  )
  init

  def init(): Unit = {
    val store: Pot[Store[IWS, IWS]] = Ready(Store(Map(
      4 -> Ready(Data(Seq(QuantityUnit("1", "QuantityUnit", 4, "QuantityUnit")))),
      6 -> Ready(Data(Seq(CostCenter("1", "CostCenter", 6, "CostCenter")))),
      7 -> Ready(Data(Seq(Article()))),
      9 -> Ready(Data(Seq(Account()))),
      8 -> Ready(Data(Seq(ArticleGroup()))),
     101 -> Ready(Data(Seq(PurchaseOrder[LinePurchaseOrder]()))),
      4711 -> Ready(Data(Seq(TodoItem())))
    )))

     model = RootModel(store, Empty)

  }
}