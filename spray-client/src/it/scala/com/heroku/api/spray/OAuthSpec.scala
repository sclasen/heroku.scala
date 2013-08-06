package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class OAuthSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for OAuth" must {
    "operate on OAuthAuthorizations" in {
      val auth = await(api.execute(OAuthAuthorization.Create(List("global"), Some("OAuthSpec")), apiKey))
      val authz = await(api.executeListAll(OAuthAuthorization.List(), apiKey))
      authz.contains(auth) must be(true)
      val info = await(api.execute(OAuthAuthorization.Info(auth.id), apiKey))
      info must equal(auth)
      await(api.execute(OAuthAuthorization.Delete(auth.id), apiKey))
    }
  }

}
