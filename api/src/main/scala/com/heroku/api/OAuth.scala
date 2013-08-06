package com.heroku.api

import com.heroku.api.Request._
import com.heroku.api.OAuthAuthorization.Grant
import com.heroku.api.OAuthAuthorization.Session
import com.heroku.api.OAuthAuthorization.AccessToken
import com.heroku.api.OAuthAuthorization.Client
import com.heroku.api.OAuthAuthorization.CreateAuthorizationClient
import com.heroku.api.OAuthAuthorization.CreateAuthorizationBody
import com.heroku.api.OAuthAuthorization.RefreshToken
import com.heroku.api.OAuthToken.Authorization

object OAuthAuthorization {

  case class AccessToken(expires_in: Option[String], id: String, token: String)

  case class Client(id: String, name: String, redirect_uri: String)

  case class Grant(code: String, expires_in: String, id: String)

  case class RefreshToken(expires_in: String, id: String, token: String)

  case class Session(id: String)

  case class CreateAuthorizationClient(id: String)

  case class CreateAuthorizationBody(scope: collection.immutable.List[String], client: Option[CreateAuthorizationClient], description: Option[String])

  case class Create(scope: collection.immutable.List[String], description: Option[String] = None, client_id: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateAuthorizationBody, OAuthAuthorization] {
    val endpoint = "/oauth/authorizations"
    val expect = expect201
    val method = POST
    val body = CreateAuthorizationBody(scope, client_id.map(CreateAuthorizationClient), description)
  }

  case class List(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[OAuthAuthorization] {
    val endpoint = "/oauth/authorizations"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[OAuthAuthorization] = this.copy(range = Some(nextRange))
  }

  case class Info(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[OAuthAuthorization] {
    val endpoint = s"/oauth/authorizations/$id"
    val expect = expect200
    val method = GET
  }

  case class Delete(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[OAuthAuthorization] {
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

  implicit def oauthAuthorizationListFromJson: FromJson[List[OAuthAuthorization]]

  implicit def oauthClientFromJson: FromJson[OAuthClient]

  implicit def oauthClientListFromJson: FromJson[List[OAuthClient]]

  implicit def oauthTokenFromJson: FromJson[OAuthToken]

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

