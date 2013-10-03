package com.heroku.platform.api

abstract class RateLimitSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: RateLimitRequestJson with RateLimitResponseJson = aj

  import implicits._

  "Api for RateLimits" must {
    "operate on RateLimits" in {
      val limit = info(RateLimit.Info)
      limit.remaining must be > 0
    }
  }

}

