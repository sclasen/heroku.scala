package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class AccountIntegrationSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "SprayApi for Account" must {
    "operate on the Account" in {
      val info = await(api.execute(Account.Info(), apiKey))
      val tracking = info.allow_tracking
      val updated = await(api.execute(Account.Update(allow_tracking = Some(!tracking)), apiKey))
      updated.allow_tracking must not be (tracking)
    }
  }

}
