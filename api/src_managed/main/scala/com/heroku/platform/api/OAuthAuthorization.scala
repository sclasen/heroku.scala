package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthAuthorization._

/** OAuth authorizations represent clients that a Heroku user has authorized to automate, customize or extend their usage of the platform. For more information please refer to the [Heroku OAuth documentation](https://devcenter.heroku.com/articles/oauth) */
object OAuthAuthorization {
  import OAuthAuthorization.models._
  object models {
    case class CreateOAuthAuthorizationBody(client: Option[String] = None, description: Option[String] = None, expires_in: Option[Int] = None, scope: Seq[String])
    case class OAuthAuthorizationAccessToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthAuthorizationRefreshToken(expires_in: Option[Int], id: String, token: String)
    case class OAuthAuthorizationClient(id: String, name: String, redirect_uri: String)
    case class OAuthAuthorizationGrant(code: String, expires_in: Int, id: String)
  }
  /** Create a new OAuth authorization. */
  case class Create(client_id: Option[String] = None, description: Option[String] = None, expires_in: Option[Int] = None, scope: Seq[String]) extends RequestWithBody[models.CreateOAuthAuthorizationBody, OAuthAuthorization] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/oauth/authorizations"
    val method: String = POST
    val body: models.CreateOAuthAuthorizationBody = models.CreateOAuthAuthorizationBody(client_id, description, expires_in, scope)
  }
  /** Delete OAuth authorization. */
  case class Delete(oauth_authorization_id: String) extends Request[OAuthAuthorization] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/authorizations/%s".format(oauth_authorization_id)
    val method: String = DELETE
  }
  /** Info for an OAuth authorization. */
  case class Info(oauth_authorization_id: String) extends Request[OAuthAuthorization] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/authorizations/%s".format(oauth_authorization_id)
    val method: String = GET
  }
  /** List OAuth authorizations. */
  case class List(range: Option[String] = None) extends ListRequest[OAuthAuthorization] {
    val endpoint: String = "/oauth/authorizations"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[OAuthAuthorization] = this.copy(range = Some(nextRange))
  }
}

/** OAuth authorizations represent clients that a Heroku user has authorized to automate, customize or extend their usage of the platform. For more information please refer to the [Heroku OAuth documentation](https://devcenter.heroku.com/articles/oauth) */
case class OAuthAuthorization(access_token: Option[models.OAuthAuthorizationAccessToken], scope: Seq[String], refresh_token: Option[models.OAuthAuthorizationRefreshToken], id: String, client: Option[models.OAuthAuthorizationClient], grant: Option[models.OAuthAuthorizationGrant], created_at: String, updated_at: String)

/** json serializers related to OAuthAuthorization */
trait OAuthAuthorizationRequestJson {
  implicit def ToJsonCreateOAuthAuthorizationBody: ToJson[models.CreateOAuthAuthorizationBody]
  implicit def ToJsonOAuthAuthorizationAccessToken: ToJson[models.OAuthAuthorizationAccessToken]
  implicit def ToJsonOAuthAuthorizationRefreshToken: ToJson[models.OAuthAuthorizationRefreshToken]
  implicit def ToJsonOAuthAuthorizationClient: ToJson[models.OAuthAuthorizationClient]
  implicit def ToJsonOAuthAuthorizationGrant: ToJson[models.OAuthAuthorizationGrant]
}

/** json deserializers related to OAuthAuthorization */
trait OAuthAuthorizationResponseJson {
  implicit def FromJsonOAuthAuthorizationAccessToken: FromJson[models.OAuthAuthorizationAccessToken]
  implicit def FromJsonOAuthAuthorizationRefreshToken: FromJson[models.OAuthAuthorizationRefreshToken]
  implicit def FromJsonOAuthAuthorizationClient: FromJson[models.OAuthAuthorizationClient]
  implicit def FromJsonOAuthAuthorizationGrant: FromJson[models.OAuthAuthorizationGrant]
  implicit def FromJsonOAuthAuthorization: FromJson[OAuthAuthorization]
  implicit def FromJsonListOAuthAuthorization: FromJson[collection.immutable.List[OAuthAuthorization]]
}