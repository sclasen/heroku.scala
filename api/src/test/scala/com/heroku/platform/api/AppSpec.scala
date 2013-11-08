package com.heroku.platform.api

abstract class AppSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: HerokuAppRequestJson with HerokuAppResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Apps" must {
    "operate on Apps" in {

      val app = execute(HerokuApp.Create())

      val appList = listAll(HerokuApp.List())

      appList.contains(app) must be(true)

      val infoByName = execute(HerokuApp.Info(app.name))
      infoByName.id must equal(app.id)
      infoByName.created_at must equal(app.created_at)

      val infoById = execute(HerokuApp.Info(app.id))
      infoById.id must equal(app.id)
      infoById.created_at must equal(app.created_at)

      val newname = s"${app.name}-foo"
      val updated = execute(HerokuApp.Update(app.id, Some(true), Some(newname)))

      updated.maintenance must be(true)
      updated.name must be(newname)

      execute(HerokuApp.Delete(updated.id))

    }
  }

}

