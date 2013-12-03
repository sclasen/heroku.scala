package com.heroku.platform.api

abstract class OAuthSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: OAuthAuthorizationRequestJson with OAuthAuthorizationResponseJson with OAuthClientRequestJson with OAuthClientResponseJson with OAuthTokenRequestJson with OAuthTokenResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for OAuth" must {
    "operate on OAuthAuthorizations" in {
      import primary._
      val auth = request(OAuthAuthorization.Create(scope = List("global"), description = Some("OAuthSpec")))
      try {
        val authz = requestAll(OAuthAuthorization.List())
        authz.map(_.id) must contain(auth.id)
        val authInfo = request(OAuthAuthorization.Info(auth.id))
        authInfo.id must equal(auth.id)
      } finally {
        request(OAuthAuthorization.Delete(auth.id))
      }
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
      import primary._
      import OAuthToken.models._
      pending /*needs to do the redirect dance*/
      /*
      val client = request(OAuthClient.Create("test-client", "https://example.com/foo"))
      val auth = request(OAuthAuthorization.Create(scope = Array("global"), description = Some("OAuthSpec"), client_id = Some(client.id)))
      auth.grant.isDefined must be(true)
      val token = request(OAuthToken.Create(OAuthTokenClient(client.secret), OAuthTokenGrant(auth.grant.get.code, "code"), OAuthTokenRefreshToken(None, auth.refresh_token.get.id, auth.refresh_token.get.token)))
      request(OAuthAuthorization.Delete(auth.id))
      request(OAuthClient.Delete(client.id)) must equal(client)
      */
    }
  }

}

