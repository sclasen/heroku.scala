package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthAuthorization._

object OAuthAuthorization {
  import OAuthAuthorization.models._
  object models {
    case class CreateOAuthAuthorizationBody(client: Option[OAuthClientIdentity] = None, description: Option[String] = None, scope: Array[String])
    case class OAuthAuthorizationAccessToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthAuthorizationRefreshToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthAuthorizationClient(id: String, name: String, redirect_uri: String)
    case class OAuthAuthorizationGrant(code: String, expires_in: Int, id: String)
  }
  case class Create(client: Option[OAuthClientIdentity] = None, description: Option[String] = None, scope: Array[String]) extends RequestWithBody[models.CreateOAuthAuthorizationBody, OAuthAuthorization] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/oauth/authorizations"
    val method: String = POST
    val body: models.CreateOAuthAuthorizationBody = models.CreateOAuthAuthorizationBody(client, description, scope)
  }
  case class Delete(oauth_authorization_id: String) extends Request[OAuthAuthorization] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/authorizations/%s".format(oauth_authorization_id)
    val method: String = DELETE
  }
  case class Info(oauth_authorization_id: String) extends Request[OAuthAuthorization] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/authorizations/%s".format(oauth_authorization_id)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[OAuthAuthorization] {
    val endpoint: String = "/oauth/authorizations"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[OAuthAuthorization] = this.copy(range = Some(nextRange))
  }
}

case class OAuthAuthorization(access_token: Option[models.OAuthAuthorizationAccessToken], refresh_token: Option[models.OAuthAuthorizationRefreshToken], id: String, client: Option[models.OAuthAuthorizationClient], grant: models.OAuthAuthorizationGrant, created_at: String, updated_at: String)

case class OAuthAuthorizationIdentity(id: Option[String])

case object OAuthAuthorizationIdentity {
  def byId(id: String) = OAuthAuthorizationIdentity(Some(id))
}

trait OAuthAuthorizationRequestJson {
  implicit def ToJsonCreateOAuthAuthorizationBody: ToJson[models.CreateOAuthAuthorizationBody]
  implicit def ToJsonOAuthAuthorizationAccessToken: ToJson[models.OAuthAuthorizationAccessToken]
  implicit def ToJsonOAuthAuthorizationRefreshToken: ToJson[models.OAuthAuthorizationRefreshToken]
  implicit def ToJsonOAuthAuthorizationClient: ToJson[models.OAuthAuthorizationClient]
  implicit def ToJsonOAuthAuthorizationGrant: ToJson[models.OAuthAuthorizationGrant]
  implicit def ToJsonOAuthAuthorizationIdentity: ToJson[OAuthAuthorizationIdentity]
}

trait OAuthAuthorizationResponseJson {
  implicit def FromJsonOAuthAuthorizationAccessToken: FromJson[models.OAuthAuthorizationAccessToken]
  implicit def FromJsonOAuthAuthorizationRefreshToken: FromJson[models.OAuthAuthorizationRefreshToken]
  implicit def FromJsonOAuthAuthorizationClient: FromJson[models.OAuthAuthorizationClient]
  implicit def FromJsonOAuthAuthorizationGrant: FromJson[models.OAuthAuthorizationGrant]
  implicit def FromJsonOAuthAuthorization: FromJson[OAuthAuthorization]
  implicit def FromJsonListOAuthAuthorization: FromJson[collection.immutable.List[OAuthAuthorization]]
}