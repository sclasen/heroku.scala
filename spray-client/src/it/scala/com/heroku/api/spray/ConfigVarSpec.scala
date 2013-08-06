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
      val config = await(api.execute(ConfigVar.Update(app.id, Map("TIMESTAMP" -> now)), apiKey))
      config("TIMESTAMP") must be(now)
      val vars = await(api.execute(ConfigVar.Info(app.id), apiKey))
      vars("TIMESTAMP") must be(now)
      val config2 = await(api.execute(ConfigVar.Update(app.id, Map("TIMESTAMP" -> null)), apiKey))
      config2.get("TIMESTAMP") must be(None)
      val vars2 = await(api.execute(ConfigVar.Info(app.id), apiKey))
      vars2.get("TIMESTAMP") must be(None)
    }
  }

}
