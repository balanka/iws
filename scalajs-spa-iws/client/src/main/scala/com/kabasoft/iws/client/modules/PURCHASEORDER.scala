package com.kabasoft.iws.client.modules

import com.kabasoft.iws.client.components.LinePurchaseOrderList.LinePurchaseOrderListProps
import com.kabasoft.iws.client.components.{LinePurchaseOrderList, PurchaseOrderList}
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import com.kabasoft.iws.gui.macros.Bootstrap._
import com.kabasoft.iws.client.logger._
import com.kabasoft.iws.shared._
import com.kabasoft.iws.gui.macros._
import scalacss.ScalaCssReact._

object PURCHASEORDER {

  case class Props(proxy: ModelProxy[Pot[Data]])
  case class State(selectedItem: Option[PurchaseOrder[LinePurchaseOrder]] = None, showForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.ifTrue(props.proxy().isEmpty, props.proxy.dispatch(Refresh (PurchaseOrder[LinePurchaseOrder]())))

    def editTodo(item:Option[PurchaseOrder[LinePurchaseOrder]]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showForm = true))

    def todoEdited(item:PurchaseOrder[LinePurchaseOrder], cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("CostCenter editing cancelled")
      } else {
        Callback.log(s"CostCenter edited: $item") >>
          $.props >>= (_.proxy.dispatch(Update(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showForm = false))
    }



    //def editLineItem(line:Option[[LinePurchaseOrder]) = $.modState(s => s.copy(selectedItem = item, showForm = true))

//     def getList (p:Props) {
//       p.proxy().foreach( all => all.items.seq).lift(0).map(x =>log.debug("PurchaseOrder :"+x))
//
//        val list = for {
//         po <- p.all().lift(0)
//         z <-po.lines
//
//    } yield z

    def render(p: Props, s: State) =
      Panel(Panel.Props("What needs to be done"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(all => PurchaseOrderList(all.items.asInstanceOf[Seq[PurchaseOrder[LinePurchaseOrder]]], item => p.proxy.dispatch(Update(item)),
          item => editTodo(Some(item)), item => p.proxy.dispatch(Delete(item)))
         // LinePurchaseOrderList(LinePurchaseOrderListProps(p.proxy().foreach( all => all.items.asInstanceOf[List[LinePurchaseOrder]]),item => p.proxy.dispatch(Update(item)),
         //   item => editTodo(Some(item)), item => p.proxy.dispatch(Delete(item)))))
           // item2=>Callback(Actions.update(item2)), item2 => B.edit2(Some(item2)), B.delete2)),
         // Button(Button.Props(p.proxy().editItem(trans), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
         // Button(Button.Props(p.deleteItem(trans), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        ),

        Button(Button.Props(editTodo(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showForm) PurchaseOrderForm(PurchaseOrderForm.Props(s.selectedItem, todoEdited))
        else // otherwise add an empty placeholder
          Seq.empty[ReactElement])
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("PURCHASEORDER")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Data]]) = component(Props(proxy))
}

object PurchaseOrderForm  {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[PurchaseOrder[LinePurchaseOrder]], submitHandler: (PurchaseOrder[LinePurchaseOrder], Boolean) => Callback)
  case class State(item: PurchaseOrder[LinePurchaseOrder], cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
      props.submitHandler(state.item, state.cancelled)
   // def updateId(e: ReactEventI) = {
    //  t.modState(s => s.copy(item = s.item.copy(id = e.currentTarget.value.toLong)))
   // }

    def updateOid(e: ReactEventI) = {
      val l =e.currentTarget.value.toLong
      log.debug(s"Oid is "+l)

      t.modState(s => s.copy(item = s.item.copy(oid = l)))
    }

    def updateStore(e: ReactEventI) = {
      val currentValue =Some(e.currentTarget.value)
      t.modState(s => s.copy(item = s.item.copy(store =currentValue )))
    }

    def updateAccount(e: ReactEventI) = {
      val currentValue =Some(e.currentTarget.value)
      t.modState(s => s.copy(item = s.item.copy(account =currentValue)))
    }


     def body (s:State): ReactElement = {
      <.div(bss.formGroup,
        <.label(^.`for` := "id", "id"),
        <.input.text(bss.formControl, ^.id := "id", ^.value := s.item.id,
          ^.placeholder := "write Id"),
        <.label(^.`for` := "iod", "oid"),
        <.input.text(bss.formControl, ^.id := "oid", ^.value := s.item.oid,
          ^.placeholder := "write oid", ^.onChange ==> updateOid),
        <.label(^.`for` := "store", "Store"),
        <.input.text(bss.formControl, ^.id := "store", ^.value := s.item.store,
          ^.placeholder := "write Store", ^.onChange ==> updateStore),
       <.label(^.`for` := "account", "Account"),
       <.input.text(bss.formControl, ^.id := "account", ^.value := s.item.account,
         ^.placeholder := "write Account", ^.onChange ==> updateAccount))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a purchase order")
      val headerText = if (s.item.id == "") "Add new purchase order" else "Edit purchase order"

      Modal(Modal.Props(
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        closed = formClosed(s, p)),
        body(s)

      )
    }
  }

  val component = ReactComponentB[Props]("PurchaseOrderForm")
    .initialState_P(p => State(p.item.getOrElse(PurchaseOrder[LinePurchaseOrder]())))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}