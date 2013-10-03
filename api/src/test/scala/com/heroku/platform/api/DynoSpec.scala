package com.heroku.platform.api

abstract class DynoSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: DynoRequestJson with DynoResponseJson = aj

  import implicits._

  "Api for Dynos" must {
    "operate on Dynos" in {
      val app = getApp
      val run = create(Dyno.Create(app.id, attach = Some(false), command = "sleep 30", size = Some(2)))
      val dynoList = listAll(Dyno.List(app.id))
      dynoList(0).id must equal(run.id)
      dynoList(0).created_at must equal(run.created_at)
      // broken no json atm  val kill = delete(Dyno.RestartDyno(app.id, run.id))
      //TODO better tests, need to create releases from a test slug

    }
  }

}

