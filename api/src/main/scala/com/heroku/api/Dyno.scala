package com.heroku.api

import com.heroku.api.Request._


case class DynoRelease(id: String)

case class Dyno(attach_url: Option[String], command: String, created_at: String, id: String, name: String, release: DynoRelease, state: String, `type`: String, updated_at: String)

case class CreateDynoBody(command: String, attach: Boolean = false)

case class CreateDyno(appId: String, command: String, attach: Boolean = false, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateDynoBody, Dyno] {
  val expect: Set[Int] = expect201
  val endpoint: String = s"/apps/$appId/dynos"
  val method: String = POST
  val body = CreateDynoBody(command, attach)
}

case class ListDynos(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Dyno] {
  val endpoint: String = s"/apps/$appId/dynos"
  val method: String = GET

  def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
}

case class DynoInfo(appId: String, dynoId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Dyno] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/dynos/$dynoId"
  val method: String = GET
}

case class DeleteDyno(appId: String, dynoId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Dyno] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/dynos/$dynoId"
  val method: String = DELETE
}

trait DynoJson{
  implicit def createDynoBodyToJson:ToJson[CreateDynoBody]
  implicit def dynoReleaseFromJson:FromJson[DynoRelease]
  implicit def dynoFromJson:FromJson[Dyno]
}


