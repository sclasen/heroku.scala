package com.heroku.api

import Request._

case class AppOwner(email: String, id: String)

case class HerokuApp(buildpack_provided_description: Option[String],
  created_at: String,
  id: String,
  git_url: String,
  maintenance: Boolean,
  name: String,
  owner: AppOwner,
  released_at: Option[String],
  repo_size: Option[Int],
  slug_size: Option[Int],
  updated_at: Option[String],
  web_url: String)

case class CreateApp(name: Option[String] = None, stack: Option[String] = Some("cedar"), region: Option[String] = None)

case class UpdateApp(maintenance: Option[Boolean] = None, name: Option[String] = None, owner: Option[String] = None)

trait HerokuAppJson {
  implicit def createAppToJson: ToJson[CreateApp]
  implicit def updateAppToJson: ToJson[UpdateApp]
  implicit def appFromJson: FromJson[HerokuApp]
  implicit def appListFromJson: FromJson[List[HerokuApp]]
}

case class AppCreate(body: CreateApp, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateApp, HerokuApp] {
  val endpoint = "/apps"
  val expect = expect201
  val method = POST
}

case class AppList(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[HerokuApp] {
  val endpoint = "/apps"
  val method = GET

  def nextRequest(nextRange:String): ListRequest[HerokuApp] = this.copy(range = Some(nextRange))
}

case class AppInfo(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = GET
}

case class AppUpdate(id: String, body: UpdateApp, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateApp, HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = PUT
}

case class AppDelete(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = DELETE
}

