package com.heroku.platform.api

abstract class DynoSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: DynoRequestJson with DynoResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Dynos" must {
    "operate on Dynos" in {
      import primary._
      val app = getApp
      val run = request(Dyno.Create(app.id, attach = Some(false), command = "sleep 30", size = Some("2")))
      val dynoList = requestAll(Dyno.List(app.id))
      dynoList(0).id must equal(run.id)
      dynoList(0).created_at must equal(run.created_at)
      request(Dyno.RestartAll(app.id))
    }
  }

}

