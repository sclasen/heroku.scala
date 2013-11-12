package com.heroku.platform.api

import com.heroku.platform.api.Request._

import RateLimit._

/** Rate Limit represents the number of request tokens each account holds. Requests to this endpoint do not count towards the rate limit. */
object RateLimit {
  import RateLimit.models._
  object models {
    ()
  }
  /** Info for rate limits. */
  case object Info extends Request[RateLimit] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/rate-limits"
    val method: String = GET
  }
}

/** Rate Limit represents the number of request tokens each account holds. Requests to this endpoint do not count towards the rate limit. */
case class RateLimit(remaining: Int)

/** json deserializers related to RateLimit */
trait RateLimitResponseJson {
  implicit def FromJsonRateLimit: FromJson[RateLimit]
  implicit def FromJsonListRateLimit: FromJson[collection.immutable.List[RateLimit]]
}