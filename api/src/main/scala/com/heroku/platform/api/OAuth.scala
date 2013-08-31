package com.heroku.platform.api

import com.heroku.platform.api.Request._

import com.heroku.platform.api.OAuthAuthorization.models._
import com.heroku.platform.api.OAuthAuthorization.models.AccessToken
import com.heroku.platform.api.OAuthAuthorization.models.CreateAuthorizationBody
import com.heroku.platform.api.OAuthAuthorization.models.Client
import com.heroku.platform.api.OAuthAuthorization.models.CreateAuthorizationClient
import com.heroku.platform.api.OAuthToken.models.Authorization

object OAuthAuthorization {

  object models {

    case class AccessToken(expires_in: Option[String], id: String, token: String)

    case class Client(id: String, name: String, redirect_uri: String)

    case class Grant(code: String, expires_in: String, id: String)

    case class RefreshToken(expires_in: String, id: String, token: String)

    case class Session(id: String)

    case class CreateAuthorizationClient(id: String)

    case class CreateAuthorizationBody(scope: collection.immutable.List[String], client: Option[CreateAuthorizationClient], description: Option[String])

  }
  case class Create(scope: collection.immutable.List[String], description: Option[String] = None, client_id: Option[String] = None) extends RequestWithBody[CreateAuthorizationBody, OAuthAuthorization] {
    val endpoint = "/oauth/authorizations"
    val expect = expect201
    val method = POST
    val body = CreateAuthorizationBody(scope, client_id.map(CreateAuthorizationClient), description)
  }

  case class List(range: Option[String] = None) extends ListRequest[OAuthAuthorization] {
    val endpoint = "/oauth/authorizations"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[OAuthAuthorization] = this.copy(range = Some(nextRange))
  }

  case class Info(id: String) extends Request[OAuthAuthorization] {
    val endpoint = s"/oauth/authorizations/$id"
    val expect = expect200
    val method = GET
  }

  case class Delete(id: String) extends Request[OAuthAuthorization] {
    val endpoint = s"/oauth/authorizations/$id"
    val expect = expect200
    val method = DELETE
  }

}

case class OAuthAuthorization(access_token: AccessToken,
    client: Option[Client],
    created_at: String,
    description: String,
    grant: Option[Grant],
    id: String,
    refresh_token: Option[RefreshToken],
    scope: collection.immutable.List[String],
    updated_at: String) {
}

trait OAuthRequestJson {
  implicit def oauthCreateAuthoriztionClient: ToJson[CreateAuthorizationClient]

  implicit def oauthcreateAuthorizationBody: ToJson[CreateAuthorizationBody]
}

trait OAuthResponseJson {
  implicit def oauthAuthorizationFromJson: FromJson[OAuthAuthorization]

  implicit def oauthAuthorizationAccessTokenFromJson: FromJson[OAuthAuthorization.models.AccessToken]

  implicit def oauthAuthorizationClientFromJson: FromJson[OAuthAuthorization.models.Client]

  implicit def oauthAuthorizationGrantFromJson: FromJson[OAuthAuthorization.models.Grant]

  implicit def oauthAuthorizationRefreshTokenFromJson: FromJson[OAuthAuthorization.models.RefreshToken]

  implicit def oauthAuthorizationSessionFromJson: FromJson[OAuthAuthorization.models.Session]

  implicit def oauthAuthorizationListFromJson: FromJson[List[OAuthAuthorization]]

  implicit def oauthClientFromJson: FromJson[OAuthClient]

  implicit def oauthClientListFromJson: FromJson[List[OAuthClient]]

  implicit def oauthTokenFromJson: FromJson[OAuthToken]

  implicit def oauthTokenAccessTokenFromJson: FromJson[OAuthToken.models.AccessToken]

  implicit def oauthTokenAuthorizationFromJson: FromJson[OAuthToken.models.Authorization]

  implicit def oauthTokenRefreshTokenFromJson: FromJson[OAuthToken.models.RefreshToken]

  implicit def oauthTokenListFromJson: FromJson[List[OAuthToken]]
}

object OAuthClient {

}

case class OAuthClient(created_at: String,
  id: String,
  name: String,
  redirect_uri: String,
  secret: String,
  updated_at: String)

object OAuthToken {

  object models {
    case class Authorization(id: String)

    case class AccessToken(expires_in: Long, id: String, token: String)

    case class RefreshToken(expires_in: Long, id: String, token: String)
  }

}

case class OAuthToken(authorization: Authorization,
  access_token: OAuthToken.models.AccessToken,
  //client:TokenClient,
  created_at: String,
  //grant:TokenGrant,
  id: String,
  refresh_token: OAuthToken.models.RefreshToken,
  session: Session,
  updated_at: String)

