package com.kabasoft.iws.client.modules

import com.kabasoft.iws.client.components.CostCenterList
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

object COSTCENTER {

  case class Props(proxy: ModelProxy[Pot[Data]])
  case class State(selectedItem: Option[CostCenter] = None, showForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.ifTrue(props.proxy().isEmpty, props.proxy.dispatch(Refresh[CostCenter] (CostCenter())))

    def editTodo(item:Option[CostCenter]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showForm = true))

    def todoEdited(item:CostCenter, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("CostCenter editing cancelled")
      } else {
        Callback.log(s"CostCenter edited: $item") >>
          $.props >>= (_.proxy.dispatch(Update[CostCenter](item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showForm = false))
    }

    def render(p: Props, s: State) =
      Panel(Panel.Props("What needs to be done"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        //p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(todos => CostCenterList(todos.items, item => p.proxy.dispatch(Update[CostCenter](item.asInstanceOf[CostCenter])),
          item => editTodo(Some(item.asInstanceOf[CostCenter])), item => p.proxy.dispatch(Delete[CostCenter](item.asInstanceOf[CostCenter])))),
        Button(Button.Props(editTodo(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showForm) CostCenterForm(CostCenterForm.Props(s.selectedItem, todoEdited))
        else // otherwise add an empty placeholder
          Seq.empty[ReactElement])
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("COSTCENTER")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Data]]) = component(Props(proxy))
}

object CostCenterForm  {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[CostCenter], submitHandler: (CostCenter, Boolean) => Callback)
  case class State(item: CostCenter, cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
      props.submitHandler(state.item, state.cancelled)

    def updateId(e: ReactEventI) =
      t.modState(s => s.copy(item = s.item.copy(id = e.target.value)))

    def updateName(e: ReactEventI) =
      t.modState(s => s.copy(item = s.item.copy(name = e.target.value)))

    def updateDescription(e: ReactEventI) =
      t.modState(s => s.copy(item = s.item.copy(description = e.target.value)))

     def body (s:State): ReactElement = {
      <.div(bss.formGroup,
        <.label(^.`for` := "id", "id"),
        <.input.text(bss.formControl, ^.id := "id", ^.value := s.item.id,
          ^.placeholder := "write Id", ^.onChange ==> updateId),
        <.label(^.`for` := "name", "Name"),
        <.input.text(bss.formControl, ^.id := "name", ^.value := s.item.name,
          ^.placeholder := "write name", ^.onChange ==> updateName),
        <.label(^.`for` := "description", "Description"),
        <.input.text(bss.formControl, ^.id := "description", ^.value := s.item.description,
          ^.placeholder := "write description", ^.onChange ==> updateDescription))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a CostCenter")
      val headerText = if (s.item.id == "") "Add new CostCenter" else "Edit CostCenter"

      Modal(Modal.Props(
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        closed = formClosed(s, p)),
        body(s)

      )
    }
  }

  val component = ReactComponentB[Props]("CostCenterForm")
    .initialState_P(p => State(p.item.getOrElse(CostCenter("0", "", 6, ""))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}