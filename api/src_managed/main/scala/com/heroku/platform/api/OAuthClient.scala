package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthClient._

/** OAuth clients are applications that Heroku users can authorize to automate, customize or extend their usage of the platform. For more information please refer to the [Heroku OAuth documentation](https://devcenter.heroku.com/articles/oauth). */
object OAuthClient {
  import OAuthClient.models._
  object models {
    case class CreateOAuthClientBody(name: String, redirect_uri: String)
    case class UpdateOAuthClientBody(name: Option[String] = None, redirect_uri: Option[String] = None)
  }
  /** Create a new OAuth client. */
  case class Create(name: String, redirect_uri: String) extends RequestWithBody[models.CreateOAuthClientBody, OAuthClient] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/oauth/clients"
    val method: String = POST
    val body: models.CreateOAuthClientBody = models.CreateOAuthClientBody(name, redirect_uri)
  }
  /** Delete OAuth client. */
  case class Delete(oauth_client_id: String) extends Request[OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = DELETE
  }
  /** Info for an OAuth client */
  case class Info(oauth_client_id: String) extends Request[OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = GET
  }
  /** List OAuth clients */
  case class List(range: Option[String] = None) extends ListRequest[OAuthClient] {
    val endpoint: String = "/oauth/clients"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[OAuthClient] = this.copy(range = Some(nextRange))
  }
  /** Update OAuth client */
  case class Update(oauth_client_id: String, name: Option[String] = None, redirect_uri: Option[String] = None) extends RequestWithBody[models.UpdateOAuthClientBody, OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = PATCH
    val body: models.UpdateOAuthClientBody = models.UpdateOAuthClientBody(name, redirect_uri)
  }
}

/** OAuth clients are applications that Heroku users can authorize to automate, customize or extend their usage of the platform. For more information please refer to the [Heroku OAuth documentation](https://devcenter.heroku.com/articles/oauth). */
case class OAuthClient(secret: String, name: String, redirect_uri: String, ignores_delinquent: Option[Boolean], id: String, created_at: String, updated_at: String)

/** json serializers related to OAuthClient */
trait OAuthClientRequestJson {
  implicit def ToJsonCreateOAuthClientBody: ToJson[models.CreateOAuthClientBody]
  implicit def ToJsonUpdateOAuthClientBody: ToJson[models.UpdateOAuthClientBody]
}

/** json deserializers related to OAuthClient */
trait OAuthClientResponseJson {
  implicit def FromJsonOAuthClient: FromJson[OAuthClient]
  implicit def FromJsonListOAuthClient: FromJson[collection.immutable.List[OAuthClient]]
}