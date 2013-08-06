package com.heroku.api

import Request._
import com.heroku.api.Account.{ PasswordChange, UpdateAccount }

case class Account(allow_tracking: Boolean,
  beta: Option[Boolean],
  created_at: String,
  email: String,
  id: String,
  last_login: String,
  updated_at: String,
  verified: Option[Boolean])

object Account {

  case class UpdateAccount(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None)

  case class PasswordChange(current_password: String, password: String)

  case class Info(extraHeaders: Map[String, String] = Map.empty) extends Request[Account] {
    val endpoint = "/account"
    val method = GET
    val expect = expect200
  }

  case class Update(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateAccount, Account] {
    val endpoint = "/account"
    val method = PATCH
    val expect = expect200
    val body = UpdateAccount(allow_tracking, email, beta)
  }

  case class PasswordChangeRequest(current_password: String, password: String, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[PasswordChange, Account] {
    val endpoint = "/account/password"
    val method = PUT
    val expect = expect200
    val body = PasswordChange(current_password, password)
  }

}

trait AccountRequestJson {
  implicit def updateAccountToJson: ToJson[UpdateAccount]

  implicit def passwordChangeToJson: ToJson[PasswordChange]
}

trait AccountResponseJson {
  implicit def accountFromJson: FromJson[Account]
}

