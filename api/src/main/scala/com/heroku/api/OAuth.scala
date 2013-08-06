package com.heroku.api

case class AuthorizationAccessToken(expires_in: String, id: String, token: String)

case class AuthorizationClient(id: String, name: String, redirect_uri: String)

case class AuthorizationGrant(code: String, expires_in: String, id: String)

case class AuthorizationRefreshToken(expires_in: String, id: String, token: String)

case class OAuthAuthorization(access_token: AuthorizationAccessToken,
  client: AuthorizationClient,
  created_at: String,
  description: String,
  grant: AuthorizationGrant,
  id: String,
  refresh_token: AuthorizationRefreshToken,
  scope: String,
  updated_at: String)

trait OAuthResponseJson {
  implicit def oauthAuthorizationFromJson: FromJson[OAuthAuthorization]
  implicit def oauthClientFromJson: FromJson[OAuthClient]
}

case class OAuthClient(created_at: String,
  id: String,
  name: String,
  redirect_uri: String,
  secret: String,
  updated_at: String)

case class TokenAuthorization(id: String)

case class TokenAccessToken(expires_in: String, id: String, token: String)

//case class TokenClient(secret:String)

//case class TokenGrant(code:String, `type`:String)

case class TokenRefreshToken(expires_in: String, id: String, token: String)

case class TokenSession(id: String)

case class OAuthToken(authorization: TokenAuthorization,
  access_token: TokenAccessToken,
  //client:TokenClient,
  created_at: String,
  //grant:TokenGrant,
  id: String,
  refresh_token: TokenRefreshToken,
  session: TokenSession,
  updated_at: String)