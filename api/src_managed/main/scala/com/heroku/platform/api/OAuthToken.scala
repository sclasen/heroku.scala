package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthToken._

object OAuthToken {
  import OAuthToken.models._
  object models {
    case class CreateOAuthTokenBody(client: Option[OAuthTokenClient] = None, grant: Option[OAuthTokenGrant] = None, refresh_token: Option[OAuthTokenRefreshToken] = None)
    case class OAuthTokenAccessToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthTokenRefreshToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthTokenAuthorization(id: String)
    case class OAuthTokenClient(secret: String)
    case class OAuthTokenGrant(code: String, `type`: String)
    case class OAuthTokenSession(id: String)
    case class OAuthTokenUser(id: String)
  }
  case class Create(client: Option[OAuthTokenClient] = None, grant: Option[OAuthTokenGrant] = None, refresh_token: Option[OAuthTokenRefreshToken] = None) extends RequestWithBody[models.CreateOAuthTokenBody, OAuthToken] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/oauth/tokens"
    val method: String = POST
    val body: models.CreateOAuthTokenBody = models.CreateOAuthTokenBody(client, grant, refresh_token)
  }
}

case class OAuthToken(access_token: models.OAuthTokenAccessToken, refresh_token: models.OAuthTokenRefreshToken, authorization: models.OAuthTokenAuthorization, id: String, client: Option[models.OAuthTokenClient], grant: models.OAuthTokenGrant, session: models.OAuthTokenSession, created_at: String, updated_at: String, user: models.OAuthTokenUser)

trait OAuthTokenRequestJson {
  implicit def ToJsonCreateOAuthTokenBody: ToJson[models.CreateOAuthTokenBody]
  implicit def ToJsonOAuthTokenAccessToken: ToJson[models.OAuthTokenAccessToken]
  implicit def ToJsonOAuthTokenRefreshToken: ToJson[models.OAuthTokenRefreshToken]
  implicit def ToJsonOAuthTokenAuthorization: ToJson[models.OAuthTokenAuthorization]
  implicit def ToJsonOAuthTokenClient: ToJson[models.OAuthTokenClient]
  implicit def ToJsonOAuthTokenGrant: ToJson[models.OAuthTokenGrant]
  implicit def ToJsonOAuthTokenSession: ToJson[models.OAuthTokenSession]
  implicit def ToJsonOAuthTokenUser: ToJson[models.OAuthTokenUser]
}

trait OAuthTokenResponseJson {
  implicit def FromJsonOAuthTokenAccessToken: FromJson[models.OAuthTokenAccessToken]
  implicit def FromJsonOAuthTokenRefreshToken: FromJson[models.OAuthTokenRefreshToken]
  implicit def FromJsonOAuthTokenAuthorization: FromJson[models.OAuthTokenAuthorization]
  implicit def FromJsonOAuthTokenClient: FromJson[models.OAuthTokenClient]
  implicit def FromJsonOAuthTokenGrant: FromJson[models.OAuthTokenGrant]
  implicit def FromJsonOAuthTokenSession: FromJson[models.OAuthTokenSession]
  implicit def FromJsonOAuthTokenUser: FromJson[models.OAuthTokenUser]
  implicit def FromJsonOAuthToken: FromJson[OAuthToken]
  implicit def FromJsonListOAuthToken: FromJson[collection.immutable.List[OAuthToken]]
}