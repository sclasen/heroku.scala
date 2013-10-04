package com.heroku.platform.api

import com.heroku.platform.api.Request._

import OAuthClient._

object OAuthClient {
  import OAuthClient.models._
  object models {
    case class CreateOAuthClientBody(name: Option[String] = None, redirect_uri: Option[String] = None)
    case class UpdateOAuthClientBody(name: Option[String] = None, redirect_uri: Option[String] = None)
  }
  case class Create(name: Option[String] = None, redirect_uri: Option[String] = None) extends RequestWithBody[models.CreateOAuthClientBody, OAuthClient] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/oauth/clients"
    val method: String = POST
    val body: models.CreateOAuthClientBody = models.CreateOAuthClientBody(name, redirect_uri)
  }
  case class Delete(oauth_client_id: String) extends Request[OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = DELETE
  }
  case class Info(oauth_client_id: String) extends Request[OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[OAuthClient] {
    val endpoint: String = "/oauth/clients"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[OAuthClient] = this.copy(range = Some(nextRange))
  }
  case class Update(oauth_client_id: String, name: Option[String] = None, redirect_uri: Option[String] = None) extends RequestWithBody[models.UpdateOAuthClientBody, OAuthClient] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/oauth/clients/%s".format(oauth_client_id)
    val method: String = PATCH
    val body: models.UpdateOAuthClientBody = models.UpdateOAuthClientBody(name, redirect_uri)
  }
}

case class OAuthClient(secret: String, name: String, redirect_uri: String, id: String, created_at: String, updated_at: String)

case class OAuthClientIdentity(id: Option[String])

case object OAuthClientIdentity {
  def byId(id: String) = OAuthClientIdentity(Some(id))
}

trait OAuthClientRequestJson {
  implicit def ToJsonCreateOAuthClientBody: ToJson[models.CreateOAuthClientBody]
  implicit def ToJsonUpdateOAuthClientBody: ToJson[models.UpdateOAuthClientBody]
  implicit def ToJsonOAuthClientIdentity: ToJson[OAuthClientIdentity]
}

trait OAuthClientResponseJson {
  implicit def FromJsonOAuthClient: FromJson[OAuthClient]
  implicit def FromJsonListOAuthClient: FromJson[collection.immutable.List[OAuthClient]]
}