package com.heroku.platform.api

import Request._
import com.heroku.platform.api.Account.{ PasswordChangeBody, UpdateBody }

case class Account(allow_tracking: Boolean,
  beta: Option[Boolean],
  created_at: String,
  email: String,
  id: String,
  last_login: String,
  updated_at: String,
  verified: Option[Boolean])

object Account {

  case class UpdateBody(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None)

  case class PasswordChangeBody(current_password: String, password: String)

  case class Info(headers: Map[String, String] = Map.empty) extends Request[Account] {
    val endpoint = "/account"
    val method = GET
    val expect = expect200
  }

  case class Update(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None) extends RequestWithBody[UpdateBody, Account] {
    val endpoint = "/account"
    val method = PATCH
    val expect = expect200
    val body = UpdateBody(allow_tracking, email, beta)
  }

  case class PasswordChangeRequest(current_password: String, password: String) extends RequestWithBody[PasswordChangeBody, Account] {
    val endpoint = "/account/password"
    val method = PUT
    val expect = expect200
    val body = PasswordChangeBody(current_password, password)
  }

}

trait AccountRequestJson {
  implicit def updateAccountToJson: ToJson[UpdateBody]

  implicit def passwordChangeToJson: ToJson[PasswordChangeBody]
}

trait AccountResponseJson {
  implicit def accountFromJson: FromJson[Account]
}

