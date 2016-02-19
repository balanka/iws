package controllers

import java.nio.ByteBuffer

import boopickle.Default._
import play.api.mvc._
import services.ApiService
import com.kabasoft.iws.shared._
import com.kabasoft.iws.shared.Model._

import scala.concurrent.ExecutionContext.Implicits.global

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

object Application extends Controller {
  implicit val bigDecimalPickler = transformPickler[scala.math.BigDecimal,String](b=> String.valueOf(b.doubleValue()),
    t =>  scala.math.BigDecimal(t))
  implicit val articleGroupPickler: Pickler[ArticleGroup] = generatePickler[ArticleGroup]
  implicit val pickler = compositePickler[IWS]
  pickler.addConcreteType[TodoItem]
  pickler.addConcreteType[CostCenter]
  pickler.addConcreteType[QuantityUnit]
  pickler.addConcreteType[Account]
  pickler.addConcreteType[Article]
  pickler.addConcreteType[Balance]
  pickler.addConcreteType[ArticleGroup]
  pickler.addConcreteType[LinePurchaseOrder]
  pickler.addConcreteType[PurchaseOrder[LinePurchaseOrder]]
  val apiService = new ApiService()


  def index = Action {
    Ok(views.html.index("SPA tutorial"))
  }

  def autowireApi(path: String) = Action.async(parse.raw) {
    implicit request =>
      println(s"Request path: $path")
     // println("Rparse.raw:"+parse.raw)
      // get the request body as Array[Byte]
      val b = request.body.asBytes(parse.UNLIMITED).get
      //println(s"Request path: $path "+path.split("/").mkString)
      // call Autowire route
      Router.route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(b)))
      ).map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        println(s"Request  data: "+data.toString)
        Ok(data)
      })
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}
