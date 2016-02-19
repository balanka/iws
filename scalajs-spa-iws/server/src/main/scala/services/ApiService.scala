package services

import java.util.{UUID, Date}

import com.kabasoft.iws.shared._
import com.kabasoft.iws.dao._
import com.kabasoft.iws.dao.DAOObjects._
import com.kabasoft.iws.shared.Model._
import com.kabasoft.iws.services._
import com.kabasoft.iws.services.Request._


class ApiService extends Api {

  val todoService = new TodoService()



   def welcome(name: String): String = s"Welcome to SPA, $name! Time is now ${new Date}"

  def create(item:IWS) ={
   item match {
     case _: Account =>MakeService.make[Account].create
     case _: Article => MakeService.make[Article].create
     case _: Supplier => MakeService.make[Supplier].create
     case _: Customer => MakeService.make[Customer].create
     case _: Store => MakeService.make[Store].create
     case _: QuantityUnit => MakeService.make[QuantityUnit].create
     case _: Vat => MakeService.make[Vat].create
     case _: CostCenter => MakeService.make[CostCenter].create
     case _: ArticleGroup => MakeService.make[ArticleGroup].create
     case _: PurchaseOrder[LinePurchaseOrder] => MakeService.make[PurchaseOrder[LinePurchaseOrder]].create
     case _: TodoItem => todoService.create(item)

   }
 }
  def insert(item:IWS) ={
   item match {
     case _: Account =>MakeService.make[Account].insert(List(item.asInstanceOf[Account]))
     case _: Article => MakeService.make[Article].insert(List(item.asInstanceOf[Article]))
     case _: Supplier => MakeService.make[Supplier].insert(List(item.asInstanceOf[Supplier]))
     case _: Customer => MakeService.make[Customer].insert(List(item.asInstanceOf[Customer]))
     case _: Store => MakeService.make[Store].insert(List(item.asInstanceOf[Store]))
     case _: QuantityUnit => MakeService.make[QuantityUnit].insert(List(item.asInstanceOf[QuantityUnit]))
     case _: Vat => MakeService.make[Vat].insert(List(item.asInstanceOf[Vat]))
     case _: CostCenter => MakeService.make[CostCenter].insert(List(item.asInstanceOf[CostCenter]))
     case _: Vat => MakeService.make[Vat].insert(List(item.asInstanceOf[Vat]))
     case _: ArticleGroup => MakeService.make[ArticleGroup].insert(List(item.asInstanceOf[ArticleGroup]))
     case _: PurchaseOrder[LinePurchaseOrder] => MakeService.make[PurchaseOrder[LinePurchaseOrder]].insert(List(item.asInstanceOf[PurchaseOrder[LinePurchaseOrder]]))
     // case _: TodoItem => todoService.all()
   }
 }
  def find(item:IWS) ={
   item match {
     case _: Account =>MakeService.make[Account].find(item.id)
     case _: Article => MakeService.make[Article].find(item.id)
     case _: Supplier => MakeService.make[Supplier].find(item.id)
     case _: Customer => MakeService.make[Customer].find(item.id)
     case _: Store => MakeService.make[Store].find(item.id)
     case _: QuantityUnit => MakeService.make[QuantityUnit].find(item.id)
     case _: Vat => MakeService.make[Vat].find(item.id)
     case _: CostCenter => MakeService.make[CostCenter].find(item.id)
     case _: ArticleGroup => MakeService.make[ArticleGroup].find(item.id)
     case _: PurchaseOrder[LinePurchaseOrder] => MakeService.make[PurchaseOrder[LinePurchaseOrder]].find(item.id)
     case _: TodoItem => todoService.all(item)
   }
 }
   def findSome(item:IWS) ={
    item.modelId match {
      case 9 =>MakeService.make[Account].findSome(item.id)

      case 1 => MakeService.make[Supplier].findSome(item.id)
      case 3 => MakeService.make[Customer].findSome(item.id)
      case 2 => MakeService.make[Store].findSome(item.id)
      case 4 => MakeService.make[QuantityUnit].findSome(item.id)
      case 5 => MakeService.make[Vat].findSome(item.id)
      case 6 => MakeService.make[CostCenter].findSome(item.id)
      case 7 => MakeService.make[Article].findSome(item.id)
      case 8 => MakeService.make[ArticleGroup].findSome(item.id)
      case 101 => MakeService.make[PurchaseOrder[LinePurchaseOrder]].findSome(item.id)

      //case 4711 _: TodoItem => todoService.all()
    }
  }
   def all(item:IWS) ={
   println(s"Sending ${item}  items")
    item.modelId match {
      case 9 =>MakeService.make[Account].all

      case 1  => MakeService.make[Supplier].all
      case 3 => MakeService.make[Customer].all
      case 2  => MakeService.make[Store].all
      case 4  => MakeService.make[QuantityUnit].all
      case 5 => MakeService.make[Vat].all
      case 6  => MakeService.make[CostCenter].all
      case 7 => MakeService.make[Article].all
      case 8 => MakeService.make[ArticleGroup].all
      case 101 => MakeService.make[PurchaseOrder[LinePurchaseOrder]].all
      case 4711  => todoService.all(item)
    }
  }
   def update(item:IWS) = {
    println(s"get ${item} items")
    item.modelId match {
      case 9 => {MakeService.make[Account].update(item.asInstanceOf[Account]); all(item)}

      case 1 => {MakeService.make[Supplier].update(item.asInstanceOf[Supplier]); all(item)}
      case 3 => {MakeService.make[Customer].update(item.asInstanceOf[Customer]); all(item)}
      case 2 => {MakeService.make[Store].update(item.asInstanceOf[Store]); all(item)}
      case 4 => {MakeService.make[QuantityUnit].update(item.asInstanceOf[QuantityUnit]); all(item)}
      case 5 => {MakeService.make[Vat].update(item.asInstanceOf[Vat]); all(item)}
      case 6  => {MakeService.make[CostCenter].update(item.asInstanceOf[CostCenter]); all(item)}
      case 7 => {MakeService.make[Article].update(item.asInstanceOf[Article]); all(item)}
      case 8 => {MakeService.make[ArticleGroup].update(item.asInstanceOf[ArticleGroup]); all(item)}
      case 101 => {MakeService.make[PurchaseOrder[LinePurchaseOrder]].update(item.asInstanceOf[PurchaseOrder[LinePurchaseOrder]]); all(item)}
      case 4711=> todoService.update(item)
    }
  }

   def delete(item: IWS) = {
     println(s"get ${item} items")
    item.modelId match {
      case 9 => {MakeService.make[Account].delete(item.id); all(item)}
      case 7 => {MakeService.make[Article].delete(item.id); all(item)}
      case 1 => {MakeService.make[Supplier].delete(item.id); all(item)}
      case 3 => {MakeService.make[Customer].delete(item.id); all(item)}
      case 2 => {MakeService.make[Store].delete(item.id); all(item)}
      case 4 => {MakeService.make[QuantityUnit].delete(item.id); all(item)}
      case 5 => {MakeService.make[Vat].delete(item.id); all(item)}
      case 6  => {MakeService.make[CostCenter].delete(item.id); all(item)}
      case 8  => {MakeService.make[ArticleGroup].delete(item.id); all(item)}
      case 101 => {MakeService.make[PurchaseOrder[LinePurchaseOrder]].delete(item.id); all(item)}

      case 4711 => todoService.delete(item)
    }
  }
}
