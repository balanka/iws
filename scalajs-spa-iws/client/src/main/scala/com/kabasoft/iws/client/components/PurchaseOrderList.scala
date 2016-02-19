package com.kabasoft.iws.client.components

import com.kabasoft.iws.gui.macros.Bootstrap.{Button, CommonStyle}
import com.kabasoft.iws.gui.macros.GlobalStyles
import com.kabasoft.iws.shared._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scalacss.ScalaCssReact._

object PurchaseOrderList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class PurchaseOrderListProps(
    items: Seq[PurchaseOrder[LinePurchaseOrder]],
    stateChange: PurchaseOrder[LinePurchaseOrder] => Callback,
    editItem: PurchaseOrder[LinePurchaseOrder] => Callback,
    deleteItem: PurchaseOrder[LinePurchaseOrder] => Callback
  )

  private val PurchaseOrderList = ReactComponentB[PurchaseOrderListProps]("PurchaseOrderList")
    .render_P(p => {
      val style = bss.listGroup
      def renderHeader = {

        <.li(style.itemOpt(CommonStyle.warning))(
          <.span("  "),
          <.span("ID"),
          <.span(" "),
          <.span("Transid"),
          <.span("    "),
          <.span("Store"),
          <.span("    "),
          <.span("Account")


        )
      }
      def renderItem(trans:PurchaseOrder[LinePurchaseOrder]) = {
        <.li(style.itemOpt(CommonStyle.warning))(
          <.span("  "),
          <.span(trans.id),
          <.span(" "),
          <.s(trans.oid),
          <.span("    "),
          <.span(trans.store),
          <.span("    "),
          <.span(trans.account),
         // <.span("    "),
         // <.span(item.quantity.toDouble),
          Button(Button.Props(p.editItem(trans), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(trans), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }
      <.ul(style.listGroup)(renderHeader)(p.items map renderItem)
    })
    .build

  def apply(items: Seq[PurchaseOrder[LinePurchaseOrder]], stateChange: PurchaseOrder[LinePurchaseOrder] => Callback, editItem:PurchaseOrder[LinePurchaseOrder] => Callback, deleteItem:PurchaseOrder[LinePurchaseOrder] => Callback) =
    PurchaseOrderList(PurchaseOrderListProps(items, stateChange, editItem, deleteItem))
}
