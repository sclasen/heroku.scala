package com.heroku.api

import Request._

case class Account(allow_tracking: Boolean, beta: Boolean, confirmed: Boolean, created_at: String, email: String, id: String, last_login: String, updated_at: String, verified: Boolean)

/*

trait AccountRequest extends Request[Account] {
  val endpoint = "/account"
}

case class AccountInfo(extraHeaders: (String, String)*) extends AccountRequest {
  val method = "GET"
  val expect = expect200
}

case class UpdateAccount(extraHeaders: (String, String)*) extends AccountRequest with RequestBody {
  val method = "PUT"
  val expect = expect200
}

*/