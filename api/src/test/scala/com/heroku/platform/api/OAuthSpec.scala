package com.heroku.platform.api

abstract class OAuthSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: OAuthAuthorizationRequestJson with OAuthAuthorizationResponseJson with OAuthClientRequestJson with OAuthClientResponseJson with OAuthTokenRequestJson with OAuthTokenResponseJson = aj

  import implicits._

  "Api for OAuth" must {
    "operate on OAuthAuthorizations" in {
      val auth = create(OAuthAuthorization.Create(scope = Array("global"), description = Some("OAuthSpec")))
      val authz = listAll(OAuthAuthorization.List())
      authz.contains(auth) must be(true)
      val authInfo = info(OAuthAuthorization.Info(auth.id))
      authInfo must equal(auth)
      delete(OAuthAuthorization.Delete(auth.id))
    }
    "operate on OAuthClients" in {
      val client = create(OAuthClient.Create("test-client", "https://example.com/foo"))
      val clients = listAll(OAuthClient.List())
      clients must contain(client)
      info(OAuthClient.Info(client.id)) must equal(client)
      delete(OAuthClient.Delete(client.id)) must equal(client)
    }

    "operate on OAuthTokens" in {
      pending
    }
  }

}

