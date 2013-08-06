package com.heroku.api

import com.heroku.api.OAuthAuthorization.RefreshToken
import com.heroku.api.OAuthAuthorization.Client
import com.heroku.api.OAuthAuthorization.Grant
import com.heroku.api.OAuthAuthorization.Session
import com.heroku.api.OAuthToken.Authorization
import com.heroku.api.OAuthAuthorization.AccessToken

object OAuthAuthorization {

  case class AccessToken(expires_in: String, id: String, token: String)

  case class Client(id: String, name: String, redirect_uri: String)

  case class Grant(code: String, expires_in: String, id: String)

  case class RefreshToken(expires_in: String, id: String, token: String)

  case class Session(id: String)

}

case class OAuthAuthorization(access_token: AccessToken,
    client: Client,
    created_at: String,
    description: String,
    grant: Grant,
    id: String,
    refresh_token: RefreshToken,
    scope: String,
    updated_at: String) {
}

trait OAuthResponseJson {
  implicit def oauthAuthorizationFromJson: FromJson[OAuthAuthorization]

  implicit def oauthClientFromJson: FromJson[OAuthClient]

  implicit def oauthTokenFromJson: FromJson[OAuthToken]
}

case class OAuthClient(created_at: String,
  id: String,
  name: String,
  redirect_uri: String,
  secret: String,
  updated_at: String)

object OAuthToken {
  case class Authorization(id: String)

  case class AccessToken(expires_in: Long, id: String, token: String)

  case class RefreshToken(expires_in: Long, id: String, token: String)
}

case class OAuthToken(authorization: Authorization,
  access_token: OAuthToken.AccessToken,
  //client:TokenClient,
  created_at: String,
  //grant:TokenGrant,
  id: String,
  refresh_token: OAuthToken.RefreshToken,
  session: Session,
  updated_at: String)

