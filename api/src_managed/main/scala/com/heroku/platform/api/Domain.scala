package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Domain._

object Domain {
  import Domain.models._
  object models {
    case class CreateDomainBody(hostname: String)
  }
  case class Create(app_id_or_name: String, hostname: String) extends RequestWithBody[models.CreateDomainBody, Domain] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/domains".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateDomainBody = models.CreateDomainBody(hostname)
  }
  case class Delete(app_id_or_name: String, domain_id_or_hostname: String) extends Request[Domain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/domains/%s".format(app_id_or_name, domain_id_or_hostname)
    val method: String = DELETE
  }
  case class Info(app_id_or_name: String, domain_id_or_hostname: String) extends Request[Domain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/domains/%s".format(app_id_or_name, domain_id_or_hostname)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Domain] {
    val endpoint: String = "/apps/%s/domains".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Domain] = this.copy(range = Some(nextRange))
  }
}

case class Domain(created_at: String, hostname: String, id: String, updated_at: String)

trait DomainRequestJson {
  implicit def ToJsonCreateDomainBody: ToJson[models.CreateDomainBody]
}

trait DomainResponseJson {
  implicit def FromJsonDomain: FromJson[Domain]
  implicit def FromJsonListDomain: FromJson[collection.immutable.List[Domain]]
}