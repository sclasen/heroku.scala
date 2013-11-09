package com.heroku.platform.api

abstract class RateLimitSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: RateLimitResponseJson = aj

  import implicits._

  "Api for RateLimits" must {
    "operate on RateLimits" in {
      import primary._
      val limit = request(RateLimit.Info)
      limit.remaining must be > 0
    }
  }

}

