package com.heroku.api

import com.heroku.api.Request._


case class DomainApp(id: String)

case class Domain(app: DomainApp, base: String, created_at: String, domain: String, id: String)

case class CreateDomainBody(domain: String)

case class CreateDomain(appId: String, domain: String, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateDomainBody, Domain] {
  val endpoint = s"/apps/$appId/domains"
  val expect = expect201
  val method = POST
  val body = CreateDomainBody(domain)
}

case class DomainList(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Domain] {
  val endpoint = s"/apps/$appId/domains"
  val method = GET

  def nextRequest(nextRange: String): ListRequest[Domain] = this.copy(range = Some(nextRange))
}

case class DomainInfo(appId: String, domainId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Domain] {
  val endpoint = s"/apps/$appId/domains/$domainId"
  val expect = expect200
  val method = GET
}

case class DeleteDomain(appId: String, domainId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Domain] {
  val endpoint = s"/apps/$appId/domains/$domainId"
  val expect = expect200
  val method = DELETE
}

trait DomainJson {
  implicit def domainAppFromJson: FromJson[DomainApp]
  implicit def domainFromJson: FromJson[Domain]
  implicit def createDomainBodyToJson: ToJson[CreateDomainBody]
}