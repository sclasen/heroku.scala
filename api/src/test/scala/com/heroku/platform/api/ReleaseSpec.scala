package com.heroku.platform.api

abstract class ReleaseSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: ReleaseRequestJson with ReleaseResponseJson = aj

  import implicits._

  "Api for Releases" must {
    "operate on Releases" in {
      pending
    }

  }

}

