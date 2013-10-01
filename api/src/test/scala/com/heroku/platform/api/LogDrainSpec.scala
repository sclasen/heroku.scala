package com.heroku.platform.api

abstract class LogDrainSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: LogDrainRequestJson with LogDrainResponseJson = aj

  import implicits._

  "Api for LogDrains" must {
    "operate on LogDrains" in {
      val app = getApp
      val created = create(LogDrain.Create(app.id, "https://example.com/foo"))
      val drains = listAll(LogDrain.List(app.name))
      drains(0) must equal(created)
      val drainByid = info(LogDrain.Info(app.id, created.id))
      drainByid must equal(created)
      val drainByUrl = info(LogDrain.Info(app.id, "https://example.com/foo"))
      drainByUrl must equal(created)
      val deleted = delete(LogDrain.Delete(app.id, created.id))
    }
  }

}

