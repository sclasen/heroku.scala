package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class AccountSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Account endpoint" must {
    "return account info" in {
      await(api.execute(Account.Info(), apiKey))
    }

    "update account info" in {
      await(api.execute(Account.Info(), apiKey)).beta
    }
  }

}
