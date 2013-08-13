package com.heroku.platform.api.client.spray


import com.heroku.platform.api._


abstract class OAuthSpec(aj: ApiRequestJson with ApiResponseJson) extends SprayApiSpec(aj) {


  val implicits:OAuthRequestJson with OAuthResponseJson = aj

  import implicits._


  "Api for OAuth" must {
    "operate on OAuthAuthorizations" in {
      val auth = create(OAuthAuthorization.Create(List("global"), Some("OAuthSpec")))
      val authz =listAll(OAuthAuthorization.List())
      authz.contains(auth) must be(true)
      val authInfo = info(OAuthAuthorization.Info(auth.id))
      authInfo must equal(auth)
      delete(OAuthAuthorization.Delete(auth.id))
    }
  }

}


class SprayOAuthSpec extends OAuthSpec(SprayJsonBoilerplate)


class PlayOAuthSpec extends OAuthSpec(PlayJsonBoilerplate)



