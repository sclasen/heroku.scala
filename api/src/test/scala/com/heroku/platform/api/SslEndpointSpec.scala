package com.heroku.platform.api

abstract class SslEndpointSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: SslEndpointRequestJson with SslEndpointResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for SslEndpoints" must {
    "operate on SslEndpoints" in {
      import primary._
      pending
    }

  }

}

