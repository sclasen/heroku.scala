package com.heroku.platform.api

import com.heroku.platform.api.Request._

import RateLimit._

object RateLimit {
  import RateLimit.models._
  object models {
    ()
  }
  case object Info extends Request[RateLimit] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/rate-limits"
    val method: String = GET
  }
}

case class RateLimit(remaining: Int)

trait RateLimitRequestJson {
  ()
}

trait RateLimitResponseJson {
  implicit def FromJsonRateLimit: FromJson[RateLimit]
  implicit def FromJsonListRateLimit: FromJson[collection.immutable.List[RateLimit]]
}