package com.kabasoft.iws.client

import com.kabasoft.iws.gui.macros.{IWSBackend, BackendMacro, IWSBackendTrait}
import com.kabasoft.iws.shared.Model._
import com.kabasoft.iws.client.modules._
import com.kabasoft.iws.shared._
import com.kabasoft.iws.client.logger._
import com.kabasoft.iws.client.services.{Store, SPACircuit}
import com.kabasoft.iws.gui.macros._
import japgolly.scalajs.react._
import japgolly.scalajs.react.{ReactDOM, React}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import diode.data.{Pot, Ready}
import diode.react.ReactPot._
import diode.react._
import diode.react.ModelProxy
import com.kabasoft.iws.gui.macros.Bootstrap._




import scala.language.reflectiveCalls

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc
  case object CostCenterLoc extends Loc
  case object AccountLoc extends Loc
  case object ArticleLoc extends Loc
  case object QuantityUnitLoc extends Loc
  case object CategoryLoc extends Loc
  case object POrderLoc extends Loc
  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    val z0 = BackendMacro.makeBackend(ArticleGroup())
    val z1 = BackendMacro.makeBackend(QuantityUnit())
    val z2 = BackendMacro.makeBackend(CostCenter())
    //val bb = SPACircuit.connect(SPACircuit.store.get.models.get(8).get.asInstanceOf[Pot[Data]])
    //log.info("/////////////////////////////////////"+SPACircuit.store.get.models.get(4))
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap(_.motd)(proxy => Dashboard(ctl, proxy,QuantityUnitLoc)))
      //| staticRoute("#todo", TodoLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(4711,Ready(Todos(Seq.empty[TodoItem]))).get.asInstanceOf[Pot[Todos]])(Todo(_)))
      | staticRoute("#qty", QuantityUnitLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(4,Ready(Data(List(QuantityUnit())))).asInstanceOf[Pot[Data]])(z1(_)))
      | staticRoute("#acc", AccountLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(9,Ready(Data(List(Account())))).asInstanceOf[Pot[Data]])(ACCOUNT(_)))
      | staticRoute("#todo", TodoLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(4711,Ready(Data(List(TodoItem())))).asInstanceOf[Pot[Data]])(Todo(_)))
      | staticRoute("#art", ArticleLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(7,Ready(Data(List(Article())))).asInstanceOf[Pot[Data]])(ARTICLE(_)))
      | staticRoute("#cat", CategoryLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(8,Ready(Data(List(ArticleGroup())))).asInstanceOf[Pot[Data]])(z0(_)))
      | staticRoute("#ord", POrderLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(101,Ready(Data(List(PurchaseOrder[LinePurchaseOrder]())))).asInstanceOf[Pot[Data]])(PURCHASEORDER(_)))

      | staticRoute("#cost", CostCenterLoc) ~> renderR(ctl => SPACircuit.connect(_.store.get.models.getOrElse(6,Ready(Data(List(CostCenter())))).asInstanceOf[Pot[Data]])(z2(_)))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          //<.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
           <.div(^.className := "collapse navbar-collapse",
            // connect menu to model, because it needs to update when the number of open todos changes
             SPACircuit.connect(_.store.get.models.getOrElse(4,Ready(Data(List(QuantityUnit())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,2,"QuantityUnit", proxy,QuantityUnitLoc,QuantityUnit())),
             SPACircuit.connect(_.store.get.models.getOrElse(9,Ready(Data(List(Account())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,3,"Account", proxy,AccountLoc,Account())),

             SPACircuit.connect(_.store.get.models.getOrElse(4711,Ready(Data(List(TodoItem())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,1,"Todo", proxy,TodoLoc,TodoItem())),
             SPACircuit.connect(_.store.get.models.getOrElse(7,Ready(Data(List(Article())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,4,"Article", proxy,ArticleLoc,Article())),
             SPACircuit.connect(_.store.get.models.getOrElse(8,Ready(Data(List(ArticleGroup())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,5,"Category", proxy,CategoryLoc,ArticleGroup())),
             SPACircuit.connect(_.store.get.models.getOrElse(101,Ready(Data(List(PurchaseOrder[LinePurchaseOrder]())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,6,"PurchaseOrder", proxy,POrderLoc,PurchaseOrder[LinePurchaseOrder]())),

             SPACircuit.connect(_.store.get.models.getOrElse(6,Ready(Data(List(CostCenter())))).map(_.asInstanceOf[Data].items.count(!_.id.isEmpty)).toOption)(proxy => MainMenu(c, r.page,7,"CostCenter", proxy,CostCenterLoc,CostCenter()))

          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    log.info("This message before  RouterConfigDsl")
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
