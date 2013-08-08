package com.heroku.platform.api

import Request._
import com.heroku.platform.api.AppTransfer.CreateTransferBody

object AppTransfer {

  case class State(state: String)

  object Accepted extends State("accepted")

  object Declined extends State("declined")

  case class App(id: Option[String] = None, name: Option[String] = None)

  case class CreateTransferBody(app: App, recipient: UserBody)

  case class Create(app: App, recipient: UserBody, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateTransferBody, AppTransfer] {
    val endpoint = s"/account/app-transfers"
    val expect = expect201
    val method = POST
    val body = CreateTransferBody(app, recipient)
  }

  case class List(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[AppTransfer] {
    val endpoint = "/account/app-transfers"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[AppTransfer] = this.copy(range = Some(nextRange))
  }

  case class Info(transferId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[AppTransfer] {
    val endpoint = s"/account/app-transfers/$transferId"
    val expect = expect200
    val method = GET
  }

  case class Update(transferId: String, state: State, extraHeaders: Map[String, String] = Map.empty) extends Request[AppTransfer] {
    val endpoint = s"/account/app-transfers/$transferId"
    val expect = expect200
    val method = PATCH
    val body = state
  }

  case class Delete(transferId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[AppTransfer] {
    val endpoint = s"/account/app-transfers/$transferId"
    val expect = expect200
    val method = DELETE
  }

}

case class AppTransfer(created_at: String,
  app: AppTransfer.App,
  id: String,
  owner: User,
  recipient: User,
  state: String,
  updated_at: String)

trait AppTransferResponseJson {

  implicit def appTransferAppFromJson: FromJson[AppTransfer.App]

  implicit def appTransferFromJson: FromJson[AppTransfer]

  implicit def appTransferListFromJson: FromJson[List[AppTransfer]]
}

trait AppTransferRequestJson {
  implicit def stateToJson: ToJson[AppTransfer.State]
  implicit def appTransferAppToJson: ToJson[AppTransfer.App]
  implicit def createTransferBodyToJson: ToJson[AppTransfer.CreateTransferBody]
}