package com.heroku.platform.api

abstract class ConfigVarSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: ConfigVarRequestJson with ConfigVarResponseJson = aj

  import implicits._

  "Api for ConfigVars" must {
    "operate on ConfigVars" in {
      val app = getApp
      val now = System.currentTimeMillis().toString
      val config = execute(ConfigVar.Update(app.id, Map("TIMESTAMP" -> now)))
      config("TIMESTAMP") must be(now)
      val vars = execute(ConfigVar.Info(app.id))
      vars("TIMESTAMP") must be(now)
      val config2 = execute(ConfigVar.Update(app.id, Map("TIMESTAMP" -> null)))
      config2.get("TIMESTAMP") must be(None)
      val vars2 = execute(ConfigVar.Info(app.id))
      vars2.get("TIMESTAMP") must be(None)
    }
  }

}

