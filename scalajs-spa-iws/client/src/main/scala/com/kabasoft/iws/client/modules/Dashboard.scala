package com.kabasoft.iws.client.modules

import com.kabasoft.iws.client.components.{Chart, ChartData, ChartDataset, Motd}
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import com.kabasoft.iws.client.SPAMain.Loc
import com.kabasoft.iws.client.components._


object Dashboard {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[String]],loc:Loc)

  // create dummy data for the chart
 // val cp = Chart.ChartProps("Test chart", Chart.BarChart, ChartData(Seq("A", "B", "C"), Seq(ChartDataset(Seq(1, 2, 3), "Data1"))))
  val cp = Chart.ChartProps("Test chart", Chart.BarChart, ChartData(Seq("A", "B", "C","D","E","F"), Seq(ChartDataset(Seq(10, 2, 14,6,8,4),"Data1","#0033FF","#1C86EE"),ChartDataset(Seq(10, 2, 3,8,6,4),"Data2"))))
  val cp1 = Chart.ChartProps("Test chart", Chart.LineChart, ChartData(Seq("A", "B", "C","D","E","F"), Seq(ChartDataset(Seq(10, 2, 15,9,8,4),"Data1","#EE3B3B","#B0E2FF"),ChartDataset(Seq(10, 2, 3,8,6,4),"Data2"))))
  val cp2 = Chart.ChartProps("Test chart", Chart.RadarChart, ChartData(Seq("A", "B", "C","D","E","F"), Seq(ChartDataset(Seq(10, 2, 12,6,8,4),"Data1","#9FB6CD","#EE3B3B"),ChartDataset(Seq(10, 2, 3,8,6,4),"Data2"))))
 //val cp3 = Chart.ChartProps("Test chart", Chart.PolarAreaChart, ChartData(Seq("A", "B", "C","D","E","F"), Seq(ChartDataset(Seq(10, 2, 12,6,8,4),"Data1","#9FB6CD","#EE3B3B"),ChartDataset(Seq(10, 2, 3,8,6,4),"Data2"))))
  val cp4 = Chart.ChartProps("Test chart", Chart.DoughnutChart, ChartData(Seq("A", "B", "C","D","E","F"), Seq(ChartDataset(Seq(10, 2, 12,6,8,4),"Data1","#9FB6CD","#EE3B3B"),ChartDataset(Seq(10, 2, 3,8,6,4),"Data2"))))

  // create the React component for Dashboard
  private val component = ReactComponentB[Props]("Dashboard")
    .render_P { case Props(router, proxy, loc) =>
      <.div(
        // header, MessageOfTheDay and chart components
        <.h2("Dashboard"),
        // use connect from ModelProxy to give Motd only partial view to the model
        proxy.connect(m => m)(Motd(_)),
        Chart(cp),
        Chart(cp1),
        Chart(cp2),
        Chart(cp4),
        // create a link to the To Do view
        <.div(router.link(loc)("Check your todos!"))
      )
    }.build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[String]], loc:Loc) = component(Props(router, proxy,loc))
}
