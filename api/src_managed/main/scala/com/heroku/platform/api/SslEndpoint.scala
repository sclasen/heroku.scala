package com.heroku.platform.api

import com.heroku.platform.api.Request._

import SslEndpoint._

/** [SSL Endpoint](https://devcenter.heroku.com/articles/ssl-endpoint) is a public address serving custom SSL cert for HTTPS traffic to a Heroku app. Note that an app must have the `ssl:endpoint` addon installed before it can provision an SSL Endpoint using these APIs. */
object SslEndpoint {
  import SslEndpoint.models._
  object models {
    case class CreateSslEndpointBody(certificate_chain: String, private_key: String)
    case class UpdateSslEndpointBody(certificate_chain: Option[String] = None, private_key: Option[String] = None, rollback: Option[Boolean] = None)
  }
  /** Create a new SSL endpoint. */
  case class Create(app_id_or_name: String, certificate_chain: String, private_key: String) extends RequestWithBody[models.CreateSslEndpointBody, SslEndpoint] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/ssl-endpoints".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateSslEndpointBody = models.CreateSslEndpointBody(certificate_chain, private_key)
  }
  /** Delete existing SSL endpoint. */
  case class Delete(app_id_or_name: String, ssl_endpoint_id_or_name: String) extends Request[SslEndpoint] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/ssl-endpoints/%s".format(app_id_or_name, ssl_endpoint_id_or_name)
    val method: String = DELETE
  }
  /** Info for existing SSL endpoint. */
  case class Info(app_id_or_name: String, ssl_endpoint_id_or_name: String) extends Request[SslEndpoint] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/ssl-endpoints/%s".format(app_id_or_name, ssl_endpoint_id_or_name)
    val method: String = GET
  }
  /** List existing SSL endpoints. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[SslEndpoint] {
    val endpoint: String = "/apps/%s/ssl-endpoints".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[SslEndpoint] = this.copy(range = Some(nextRange))
  }
  /** Update an existing SSL endpoint. */
  case class Update(app_id_or_name: String, ssl_endpoint_id_or_name: String, certificate_chain: Option[String] = None, private_key: Option[String] = None, rollback: Option[Boolean] = None) extends RequestWithBody[models.UpdateSslEndpointBody, SslEndpoint] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/ssl-endpoints/%s".format(app_id_or_name, ssl_endpoint_id_or_name)
    val method: String = PATCH
    val body: models.UpdateSslEndpointBody = models.UpdateSslEndpointBody(certificate_chain, private_key, rollback)
  }
}

/** [SSL Endpoint](https://devcenter.heroku.com/articles/ssl-endpoint) is a public address serving custom SSL cert for HTTPS traffic to a Heroku app. Note that an app must have the `ssl:endpoint` addon installed before it can provision an SSL Endpoint using these APIs. */
case class SslEndpoint(cname: String, name: String, certificate_chain: String, private_key: String, rollback: Boolean, id: String, created_at: String, updated_at: String)

/** json serializers related to SslEndpoint */
trait SslEndpointRequestJson {
  implicit def ToJsonCreateSslEndpointBody: ToJson[models.CreateSslEndpointBody]
  implicit def ToJsonUpdateSslEndpointBody: ToJson[models.UpdateSslEndpointBody]
}

/** json deserializers related to SslEndpoint */
trait SslEndpointResponseJson {
  implicit def FromJsonSslEndpoint: FromJson[SslEndpoint]
  implicit def FromJsonListSslEndpoint: FromJson[collection.immutable.List[SslEndpoint]]
}