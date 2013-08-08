package com.heroku.platform.api.client.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._
import SprayApiJson._


class OAuthSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for OAuth" must {
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
