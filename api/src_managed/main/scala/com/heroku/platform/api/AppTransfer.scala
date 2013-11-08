package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AppTransfer._

object AppTransfer {
  import AppTransfer.models._
  object models {
    case class CreateAppTransferBody(app: String, recipient: String)
    case class UpdateAppTransferBody(state: String)
    case class AppTransferRecipient(email: String, id: String)
    case class AppTransferApp(name: String, id: String)
    case class AppTransferOwner(email: String, id: String)
  }
  case class Create(app_id_or_name: String, recipient_email_or_id: String) extends RequestWithBody[models.CreateAppTransferBody, AppTransfer] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/account/app-transfers"
    val method: String = POST
    val body: models.CreateAppTransferBody = models.CreateAppTransferBody(app_id_or_name, recipient_email_or_id)
  }
  case class Delete(app_transfer_id: String) extends Request[AppTransfer] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/app-transfers/%s".format(app_transfer_id)
    val method: String = DELETE
  }
  case class Info(app_transfer_id: String) extends Request[AppTransfer] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/app-transfers/%s".format(app_transfer_id)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[AppTransfer] {
    val endpoint: String = "/account/app-transfers"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AppTransfer] = this.copy(range = Some(nextRange))
  }
  case class Update(app_transfer_id: String, state: String) extends RequestWithBody[models.UpdateAppTransferBody, AppTransfer] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/app-transfers/%s".format(app_transfer_id)
    val method: String = PATCH
    val body: models.UpdateAppTransferBody = models.UpdateAppTransferBody(state)
  }
}

case class AppTransfer(recipient: models.AppTransferRecipient, state: String, id: String, app: models.AppTransferApp, created_at: String, owner: models.AppTransferOwner, updated_at: String)

trait AppTransferRequestJson {
  implicit def ToJsonCreateAppTransferBody: ToJson[models.CreateAppTransferBody]
  implicit def ToJsonUpdateAppTransferBody: ToJson[models.UpdateAppTransferBody]
  implicit def ToJsonAppTransferRecipient: ToJson[models.AppTransferRecipient]
  implicit def ToJsonAppTransferApp: ToJson[models.AppTransferApp]
  implicit def ToJsonAppTransferOwner: ToJson[models.AppTransferOwner]
}

trait AppTransferResponseJson {
  implicit def FromJsonAppTransferRecipient: FromJson[models.AppTransferRecipient]
  implicit def FromJsonAppTransferApp: FromJson[models.AppTransferApp]
  implicit def FromJsonAppTransferOwner: FromJson[models.AppTransferOwner]
  implicit def FromJsonAppTransfer: FromJson[AppTransfer]
  implicit def FromJsonListAppTransfer: FromJson[collection.immutable.List[AppTransfer]]
}