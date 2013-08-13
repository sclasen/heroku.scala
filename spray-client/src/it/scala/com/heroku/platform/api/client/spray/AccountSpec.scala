package com.heroku.platform.api.client.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._


abstract class AccountSpec extends WordSpec with SprayApiSpec with MustMatchers {

  val accountImplicits:AccountRequestJson with AccountResponseJson

  import accountImplicits._

  "SprayApi for Account" must {
    "operate on the Account" in {
      val acct = info(Account.Info())
      val tracking = acct.allow_tracking
      val updated = update(Account.Update(allow_tracking = Some(!tracking)))
      updated.allow_tracking must not be (tracking)
    }
  }

}

class SprayAccountSpec extends AccountSpec{
  val accountImplicits: AccountRequestJson with AccountResponseJson = SprayJsonBoilerplate
}


