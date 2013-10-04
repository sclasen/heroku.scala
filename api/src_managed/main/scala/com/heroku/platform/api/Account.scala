package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Account._

object Account {
  import Account.models._
  object models {
    case class UpdateAccountBody(allow_tracking: Option[Boolean] = None, beta: Option[Boolean] = None)
    case class ChangeEmailAccountBody(current_password: String, email: String)
    case class ChangePasswordAccountBody(current_password: String, password: String)
  }
  case object Info extends Request[Account] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account"
    val method: String = GET
  }
  case class Update(allow_tracking: Option[Boolean] = None, beta: Option[Boolean] = None) extends RequestWithBody[models.UpdateAccountBody, Account] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account"
    val method: String = PATCH
    val body: models.UpdateAccountBody = models.UpdateAccountBody(allow_tracking, beta)
  }
  case class ChangeEmail(current_password: String, email: String) extends RequestWithBody[models.ChangeEmailAccountBody, Account] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account"
    val method: String = PATCH
    val body: models.ChangeEmailAccountBody = models.ChangeEmailAccountBody(current_password, email)
  }
  case class ChangePassword(current_password: String, password: String) extends RequestWithBody[models.ChangePasswordAccountBody, Account] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account"
    val method: String = PATCH
    val body: models.ChangePasswordAccountBody = models.ChangePasswordAccountBody(current_password, password)
  }
}

case class Account(email: String, allow_tracking: Boolean, id: String, beta: Option[Boolean], created_at: String, last_login: String, updated_at: String, verified: Boolean)

case class AccountIdentity(email: Option[String], id: Option[String])

case object AccountIdentity {
  def byEmail(email: String) = AccountIdentity(Some(email), None)
  def byId(id: String) = AccountIdentity(None, Some(id))
}

trait AccountRequestJson {
  implicit def ToJsonUpdateAccountBody: ToJson[models.UpdateAccountBody]
  implicit def ToJsonChangeEmailAccountBody: ToJson[models.ChangeEmailAccountBody]
  implicit def ToJsonChangePasswordAccountBody: ToJson[models.ChangePasswordAccountBody]
  implicit def ToJsonAccountIdentity: ToJson[AccountIdentity]
}

trait AccountResponseJson {
  implicit def FromJsonAccount: FromJson[Account]
  implicit def FromJsonListAccount: FromJson[collection.immutable.List[Account]]
}