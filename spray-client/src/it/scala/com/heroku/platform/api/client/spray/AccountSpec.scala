package com.heroku.platform.api.client.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._
import com.heroku.platform.api.client.spray.SprayApi._

class AccountSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "SprayApi for Account" must {
    "operate on the Account" in {
      val acct = info(Account.Info())
      val tracking = acct.allow_tracking
      val updated = update(Account.Update(allow_tracking = Some(!tracking)))
      updated.allow_tracking must not be (tracking)
    }
  }

}
