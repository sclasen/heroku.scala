package com.heroku.platform.api

abstract class OAuthSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: OAuthAuthorizationRequestJson with OAuthAuthorizationResponseJson with OAuthClientRequestJson with OAuthClientResponseJson with OAuthTokenRequestJson with OAuthTokenResponseJson = aj

  import implicits._

  "Api for OAuth" must {
    "operate on OAuthAuthorizations" in {
      import primary._
      val auth = request(OAuthAuthorization.Create(scope = Array("global"), description = Some("OAuthSpec")))
      val authz = requestAll(OAuthAuthorization.List())
      authz.contains(auth) must be(true)
      val authInfo = request(OAuthAuthorization.Info(auth.id))
      authInfo must equal(auth)
      request(OAuthAuthorization.Delete(auth.id))
    }
    "operate on OAuthClients" in {
      import primary._
      val client = request(OAuthClient.Create("test-client", "https://example.com/foo"))
      val clients = requestAll(OAuthClient.List())
      clients must contain(client)
      request(OAuthClient.Info(client.id)) must equal(client)
      request(OAuthClient.Delete(client.id)) must equal(client)
    }

    "operate on OAuthTokens" in {
      pending
    }
  }

}

