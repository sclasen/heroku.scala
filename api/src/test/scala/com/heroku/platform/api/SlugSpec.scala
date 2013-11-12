package com.heroku.platform.api

abstract class SlugSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: SlugRequestJson with SlugResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Slugs" must {
    "operate on Slugs" in {
      import primary._
      val app = getApp
      val slug = request(Slug.Create(app.id, None, Map("clock" -> "bin/clock", "echo" -> "bin/echo")))
      println(slug.blob)
      val info = request(Slug.Info(app.id, slug.id))
      info.id must equal(slug.id)
    }

  }

}

