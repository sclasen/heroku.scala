package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Domain._

/** Domains define what web routes should be routed to an app on Heroku. */
object Domain {
  import Domain.models._
  object models {
    case class CreateDomainBody(hostname: String)
  }
  /** Create a new domain. */
  case class Create(app_id_or_name: String, hostname: String) extends RequestWithBody[models.CreateDomainBody, Domain] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/domains".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateDomainBody = models.CreateDomainBody(hostname)
  }
  /** Delete an existing domain */
  case class Delete(app_id_or_name: String, domain_id_or_hostname: String) extends Request[Domain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/domains/%s".format(app_id_or_name, domain_id_or_hostname)
    val method: String = DELETE
  }
  /** Info for existing domain. */
  case class Info(app_id_or_name: String, domain_id_or_hostname: String) extends Request[Domain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/domains/%s".format(app_id_or_name, domain_id_or_hostname)
    val method: String = GET
  }
  /** List existing domains. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Domain] {
    val endpoint: String = "/apps/%s/domains".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Domain] = this.copy(range = Some(nextRange))
  }
}

/** Domains define what web routes should be routed to an app on Heroku. */
case class Domain(created_at: String, hostname: String, id: String, updated_at: String)

/** json serializers related to Domain */
trait DomainRequestJson {
  implicit def ToJsonCreateDomainBody: ToJson[models.CreateDomainBody]
}

/** json deserializers related to Domain */
trait DomainResponseJson {
  implicit def FromJsonDomain: FromJson[Domain]
  implicit def FromJsonListDomain: FromJson[collection.immutable.List[Domain]]
}