package com.heroku.platform.api

abstract class AccountSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AccountRequestJson with AccountResponseJson = aj

  import implicits._

  "Api for Account" must {
    "operate on the Account" in {
      val acct = execute(Account.Info)
      val tracking = acct.allow_tracking
      val updated = execute(Account.Update(password = "foo", allow_tracking = Some(!tracking)))
      updated.allow_tracking must not be (tracking)
    }
  }

}

