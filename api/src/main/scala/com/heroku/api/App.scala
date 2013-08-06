package com.heroku.api

import Request._
import com.heroku.api.HerokuApp.{ UpdateAppBody, CreateAppBody, AppOwner, AppRegion }

object HerokuApp {

  case class AppOwner(id: Option[String] = None, email: Option[String] = None) {
    if (id.isEmpty && email.isEmpty) throw new IllegalStateException("Need to define either id or email")
  }

  case class AppRegion(name: Option[String] = None, id: Option[String] = None)

  object EU extends AppRegion(name = Some("eu"))

  object US extends AppRegion(name = Some("us"))

  case class CreateAppBody(name: Option[String] = None, stack: Option[String] = Some("cedar"), region: Option[AppRegion] = None)

  case class UpdateAppBody(maintenance: Option[Boolean] = None, name: Option[String] = None, owner: Option[AppOwner] = None)

  case class Create(name: Option[String] = None, stack: Option[String] = Some("cedar"), region: Option[AppRegion] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CreateAppBody, HerokuApp] {
    val endpoint = "/apps"
    val expect = expect201
    val method = POST
    val body = CreateAppBody(name, stack, region)
  }

  case class List(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[HerokuApp] {
    val endpoint = "/apps"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[HerokuApp] = this.copy(range = Some(nextRange))
  }

  case class Info(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
    val endpoint = s"/apps/$id"
    val expect = expect200
    val method = GET
  }

  case class Update(id: String, maintenance: Option[Boolean] = None, name: Option[String] = None, owner: Option[AppOwner] = None, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateAppBody, HerokuApp] {
    val endpoint = s"/apps/$id"
    val expect = expect200
    val method = PATCH
    val body = UpdateAppBody(maintenance, name, owner)
  }

  case class Delete(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[HerokuApp] {
    val endpoint = s"/apps/$id"
    val expect = expect200
    val method = DELETE
  }

}

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

trait HerokuAppResponseJson {

  implicit def appFromJson: FromJson[HerokuApp]

  implicit def appListFromJson: FromJson[List[HerokuApp]]

}

trait HerokuAppRequestJson {
  implicit def createAppBodyToJson: ToJson[CreateAppBody]

  implicit def updateAppBodyToJson: ToJson[UpdateAppBody]

  implicit def appOwnerToJson: ToJson[AppOwner]
}

