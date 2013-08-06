package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class ConfigVarSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for ConfigVars" must {
    "operate on ConfigVars" in {
      val app = getApp
      val now = System.currentTimeMillis().toString
      val config = update(ConfigVar.Update(app.id, Map("TIMESTAMP" -> now)))
      config("TIMESTAMP") must be(now)
      val vars = info(ConfigVar.Info(app.id))
      vars("TIMESTAMP") must be(now)
      val config2 = update(ConfigVar.Update(app.id, Map("TIMESTAMP" -> null)))
      config2.get("TIMESTAMP") must be(None)
      val vars2 = info(ConfigVar.Info(app.id))
      vars2.get("TIMESTAMP") must be(None)
    }
  }

}
