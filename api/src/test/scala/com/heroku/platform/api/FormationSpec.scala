package com.heroku.platform.api

abstract class FormationSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: FormationRequestJson with FormationResponseJson with SlugRequestJson with SlugResponseJson with ReleaseRequestJson with ReleaseResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Formations" must {
    "operate on Formations" in {
      import primary._
      val app = getApp
      val formations = requestAll(Formation.List(app.name))
      formations.size must equal(0)

      val slug = request(Slug.Create(app.id, None, Map("clock" -> "bin/clock", "echo" -> "bin/echo")))
      val putUrl = slug.blob("put")
      val curlProc = scala.sys.process.Process(s"""curl -X PUT -H Content-Type: --data-binary @api/src/test/resources/test-slug/test-slug.tgz $putUrl""")
      (curlProc !) must equal(0)

      val releasedSlug = request(Release.Create(app.id, Some("test slug release"), slug.id))
      val updatedReleases = requestAll(Release.List(app.id))
      updatedReleases.last must equal(releasedSlug)

      val clock = request(Formation.Update(app.id, "clock", Some(1)))
      clock.`type` must equal("clock")
      clock.command must equal("bin/clock")
      val echo = request(Formation.Update(app.id, "echo", Some(1)))
      echo.`type` must equal("echo")
      echo.command must equal("bin/echo")

      val formationsUpdated = requestAll(Formation.List(app.name))
      formationsUpdated.size must equal(2)
      formationsUpdated.map(_.id) must contain(clock.id)
      formationsUpdated.map(_.id) must contain(echo.id)
    }
  }

}

