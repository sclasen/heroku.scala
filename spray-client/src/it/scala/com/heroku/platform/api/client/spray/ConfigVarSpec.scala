package com.heroku.platform.api.client.spray


import com.heroku.platform.api._

abstract class ConfigVarSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: ConfigVarRequestJson with ConfigVarResponseJson = aj

  import implicits._


  "Api for ConfigVars" must {
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

