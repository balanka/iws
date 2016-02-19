package com.kabasoft.iws.client.components

import com.kabasoft.iws.shared.Account
import com.kabasoft.iws.shared._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import com.kabasoft.iws.gui.macros.Bootstrap.{Button, CommonStyle}
import com.kabasoft.iws.gui.macros.GlobalStyles

import scalacss.ScalaCssReact._


object AccountList {
  @inline private def bss = GlobalStyles.bootstrapStyles
  //val formater =NumberFormat.getIntegerInstance(new java.util.Locale("de", "DE"))
  case class AccountListProps(
    items: Seq[IWS],
    stateChange: IWS => Callback,
    editItem: IWS => Callback,
    deleteItem: IWS => Callback
  )

  private val AccountList = ReactComponentB[AccountListProps]("AccountList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: Account) = {
        <.li(style.itemOpt(CommonStyle.info),
          <.span(" "),
          <.s(item.id),
          <.span(" "),
          <.span(item.name),
          <.span(" "),
          <.s(item.description),
          <.span(" "),
          <.s(item.dateOfOpen.get.toString),
          <.span(" "),
          <.s("%06.2f".format(item.balance.amount.toDouble)),
          Button(Button.Props(p.editItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }
      <.ul(style.listGroup)(p.items.asInstanceOf[Seq[Account]] map renderItem)
    })
    .build

  def apply(items: Seq[IWS], stateChange: IWS => Callback, editItem: IWS => Callback, deleteItem: IWS => Callback) =
    AccountList(AccountListProps(items, stateChange, editItem, deleteItem))
}
