package com.heroku.api

import Request._

case class AppOwner(id: Option[String] = None, email: Option[String] = None) {
  if (id.isEmpty && email.isEmpty) throw new IllegalStateException("Need to define either id or email")
}

case class AppRegion(name: String, id: String)

case class HerokuApp(buildpack_provided_description: Option[String],
  created_at: String,
  id: String,
  git_url: String,
  maintenance: Boolean,
  name: String,
  owner: User,
  region: AppRegion,
  released_at: Option[String],
  repo_size: Option[Int],
  slug_size: Option[Int],
  updated_at: Option[String],
  web_url: String)

case class CreateAppBody(name: Option[String] = None, stack: Option[String] = Some("cedar"), region: Option[String] = None)

case class UpdateAppBody(maintenance: Option[Boolean] = None, name: Option[String] = None, owner: Option[AppOwner] = None)

trait HerokuAppJson {
  implicit def createAppBodyToJson: ToJson[CreateAppBody]

  implicit def updateAppBodyToJson: ToJson[UpdateAppBody]

  implicit def appFromJson: FromJson[HerokuApp]

  implicit def appListFromJson: FromJson[List[HerokuApp]]

  implicit def appOwnerToJson: ToJson[AppOwner]
}

case class AppCreate(name: Option[String] = None, stack: Option[String] = Some("cedar"), region: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateAppBody, HerokuApp] {
  val endpoint = "/apps"
  val expect = expect201
  val method = POST
  val body = CreateAppBody(name, stack, region)
}

case class AppList(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[HerokuApp] {
  val endpoint = "/apps"
  val method = GET

  def nextRequest(nextRange: String): ListRequest[HerokuApp] = this.copy(range = Some(nextRange))
}

case class AppInfo(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = GET
}

case class AppUpdate(id: String, maintenance: Option[Boolean] = None, name: Option[String] = None, owner: Option[AppOwner] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateAppBody, HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = PATCH
  val body = UpdateAppBody(maintenance, name, owner)
}

case class AppDelete(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
  val endpoint = s"/apps/$id"
  val expect = expect200
  val method = DELETE
}

