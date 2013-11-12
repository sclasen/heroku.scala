package com.heroku.platform.api

abstract class AccountSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AccountRequestJson with AccountResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Account" must {
    "operate on the Account" in {
      import primary._
      val acct = request(Account.Info)
      val tracking = acct.allow_tracking
      val updated = request(Account.Update(password = primaryTestPassword, allow_tracking = Some(!tracking)))
      updated.allow_tracking must not be (tracking)
    }
  }

}

