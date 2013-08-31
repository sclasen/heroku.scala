package com.heroku.platform.api

import com.heroku.platform.api.Request._
import com.heroku.platform.api.Domain.CreateDomainBody

object Domain {
  case class CreateDomainBody(hostname: String)

  case class Create(appId: String, hostname: String) extends RequestWithBody[CreateDomainBody, Domain] {
    val endpoint = s"/apps/$appId/domains"
    val expect = expect201
    val method = POST
    val body = CreateDomainBody(hostname)
  }

  case class List(appId: String, range: Option[String] = None) extends ListRequest[Domain] {
    val endpoint = s"/apps/$appId/domains"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[Domain] = this.copy(range = Some(nextRange))
  }

  case class Info(appId: String, domainId: String) extends Request[Domain] {
    val endpoint = s"/apps/$appId/domains/$domainId"
    val expect = expect200
    val method = GET
  }

  case class Delete(appId: String, domainId: String) extends Request[Domain] {
    val endpoint = s"/apps/$appId/domains/$domainId"
    val expect = expect200
    val method = DELETE
  }
}

case class Domain(updated_at: String, created_at: String, hostname: String, id: String)

trait DomainResponseJson {
  implicit def domainFromJson: FromJson[Domain]

  implicit def domainListFromJson: FromJson[List[Domain]]
}

trait DomainRequestJson {
  implicit def createDomainBodyToJson: ToJson[CreateDomainBody]
}