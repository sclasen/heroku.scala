package com.heroku.api

import Request._

case class Account(allow_tracking: Boolean,
  beta: Option[Boolean],
  created_at: String,
  email: String,
  id: String,
  last_login: String,
  updated_at: String,
  verified: Option[Boolean])

case class UpdateAccount(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None)

trait AccountRequestJson {
  implicit def updateAccountToJson: ToJson[UpdateAccount]
}

trait AccountResponseJson {
  implicit def accountFromJson: FromJson[Account]
}

case class AccountInfo(extraHeaders: Map[String, String] = Map.empty) extends Request[Account] {
  val endpoint = "/account"
  val method = GET
  val expect = expect200
}

case class AccountUpdate(allow_tracking: Option[Boolean] = None, email: Option[String] = None, beta: Option[Boolean] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateAccount, Account] {
  val endpoint = "/account"
  val method = PATCH
  val expect = expect200
  val body = UpdateAccount(allow_tracking, email, beta)
}

