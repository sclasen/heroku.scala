package com.heroku.api

import Request._

case class Account(allow_tracking: Boolean,
                   beta: Boolean,
                   confirmed: Boolean,
                   created_at: String,
                   email: String,
                   id: String,
                   last_login: Double,
                   updated_at: String,
                   verified: Boolean)

case class UpdateAccount(allow_tracking: Option[Boolean] = None, email: Option[String] = None)

case class AccountInfo(extraHeaders: Map[String, String] = Map.empty) extends Request[Account] {
  val endpoint = "/account"
  val method = "GET"
  val expect = expect200
}

case class AccountUpdate(body: UpdateAccount, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateAccount, Account] {
  val endpoint = "/account"
  val method = "PUT"
  val expect = expect200
}

