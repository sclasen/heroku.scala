package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthGrant._

object OAuthGrant {
  import OAuthGrant.models._
  object models {
    ()
  }
}

case class OAuthGrant

trait OAuthGrantRequestJson {
  ()
}

trait OAuthGrantResponseJson {
  implicit def FromJsonOAuthGrant: FromJson[OAuthGrant]
  implicit def FromJsonListOAuthGrant: FromJson[collection.immutable.List[OAuthGrant]]
}