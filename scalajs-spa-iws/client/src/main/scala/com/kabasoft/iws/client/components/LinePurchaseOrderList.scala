package com.kabasoft.iws.client.components

import com.kabasoft.iws.gui.macros.Bootstrap.{Button, CommonStyle}
import com.kabasoft.iws.gui.macros.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import com.kabasoft.iws.shared._
import scalacss.ScalaCssReact._

object LinePurchaseOrderList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class LinePurchaseOrderListProps(
    items: Seq[LinePurchaseOrder],
    stateChange: LinePurchaseOrder => Callback,
    editItem: LinePurchaseOrder => Callback,
    deleteItem: LinePurchaseOrder => Callback
  )

  private val LinePurchaseOrderList = ReactComponentB[LinePurchaseOrderListProps]("PurchaseOrderList")
    .render_P(p => {
      val style = bss.listGroup
      def renderHeader = {

        <.li(style.itemOpt(CommonStyle.warning))(
          <.span("  "),
          <.span("ID"),
          <.span(" "),
          <.span("Transid"),
          <.span("    "),
          <.span("item"),
          <.span("    "),
          <.span("Price"),
          <.span("    "),
          <.span("Quantity")

        )
      }
      def renderItem(item:LinePurchaseOrder) = {
        <.li(style.itemOpt(CommonStyle.warning))(
          <.span("  "),
          <.span(item.id),
          <.span(" "),
          <.s(item.transid),
          <.span("    "),
          <.span(item.item),
          <.span("    "),
          <.span(item.price.toDouble),
          <.span("    "),
          <.span(item.quantity.toDouble),
          Button(Button.Props(p.editItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }
      <.ul(style.listGroup)(renderHeader)(p.items map renderItem)
    })
    .build

  def apply(items: Seq[LinePurchaseOrder], stateChange: LinePurchaseOrder => Callback, editItem: LinePurchaseOrder => Callback, deleteItem:LinePurchaseOrder => Callback) =
    LinePurchaseOrderList(LinePurchaseOrderListProps(items, stateChange, editItem, deleteItem))
}
