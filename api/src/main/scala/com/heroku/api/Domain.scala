package com.heroku.api

import com.heroku.api.Request._
import com.heroku.api.Domain.CreateDomainBody

object Domain {
  case class CreateDomainBody(domain: String)

  case class Create(appId: String, domain: String, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateDomainBody, Domain] {
    val endpoint = s"/apps/$appId/domains"
    val expect = expect201
    val method = POST
    val body = CreateDomainBody(domain)
  }

  case class List(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Domain] {
    val endpoint = s"/apps/$appId/domains"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[Domain] = this.copy(range = Some(nextRange))
  }

  case class Info(appId: String, domainId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Domain] {
    val endpoint = s"/apps/$appId/domains/$domainId"
    val expect = expect200
    val method = GET
  }

  case class Domain(appId: String, domainId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Domain] {
    val endpoint = s"/apps/$appId/domains/$domainId"
    val expect = expect200
    val method = DELETE
  }
}

case class Domain(updated_at: String, created_at: String, hostname: String, id: String)

trait DomainResponseJson {
  implicit def domainFromJson: FromJson[Domain]
}

trait DomainRequestJson {
  implicit def createDomainBodyToJson: ToJson[CreateDomainBody]
}