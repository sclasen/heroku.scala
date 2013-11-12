package com.heroku.platform.api

abstract class FormationSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: FormationRequestJson with FormationResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Formations" must {
    "operate on Formations" in {
      import primary._
      val app = getApp
      val formations = requestAll(Formation.List(app.name))
      //TODO better tests, need to create releases from a test slug
      formations.size must equal(0)
    }
  }

}

