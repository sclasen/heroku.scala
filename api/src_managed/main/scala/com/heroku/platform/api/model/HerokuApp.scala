package com.heroku.platform.api.model

import com.heroku.platform.api._

import com.heroku.platform.api.Request._

import HerokuApp._

object HerokuApp {
  import HerokuApp.models._
  object models {
    case class CreateHerokuAppBody(name: Option[String] = None, region: Option[HerokuAppRegion] = None, stack: Option[HerokuAppStack] = None)
    case class UpdateHerokuAppBody(maintenance: Option[Boolean] = None, name: Option[String] = None)
    case class HerokuAppStack(id: String = null, name: String = null)
    case class HerokuAppRegion(id: String = null, name: String = null)
    case class HerokuAppOwner(email: String = null, id: String = null)
  }
  case class Create(name: Option[String] = None, region: Option[HerokuAppRegion] = None, stack: Option[HerokuAppStack] = None, headers: Map[String, String] = Map.empty) extends RequestWithBody[models.CreateHerokuAppBody, HerokuApp] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps"
    val method: String = POST
    val body: models.CreateHerokuAppBody = models.CreateHerokuAppBody(name, region, stack)
  }
  case class Delete(herokuapp_id_or_name: String, headers: Map[String, String] = Map.empty) extends Request[HerokuApp] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s".format(herokuapp_id_or_name)
    val method: String = DELETE
  }
  case class Info(herokuapp_id_or_name: String, headers: Map[String, String] = Map.empty) extends Request[HerokuApp] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s".format(herokuapp_id_or_name)
    val method: String = GET
  }
  case class List(range: Option[String] = None, headers: Map[String, String] = Map.empty) extends ListRequest[HerokuApp] {
    val endpoint: String = "/apps"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[HerokuApp] = this.copy(range = Some(nextRange))
  }
  case class Update(herokuapp_id_or_name: String, maintenance: Option[Boolean] = None, name: Option[String] = None, headers: Map[String, String] = Map.empty) extends RequestWithBody[models.UpdateHerokuAppBody, HerokuApp] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s".format(herokuapp_id_or_name)
    val method: String = PATCH
    val body: models.UpdateHerokuAppBody = models.UpdateHerokuAppBody(maintenance, name)
  }
}

case class HerokuApp(name: String, repo_size: Option[Int], git_url: String, slug_size: Option[Int], maintenance: Boolean, id: String, released_at: Option[String], web_url: String, stack: models.HerokuAppStack, region: models.HerokuAppRegion, created_at: String, owner: models.HerokuAppOwner, updated_at: String, archived_at: Option[String], buildpack_provided_description: Option[String])

trait HerokuAppRequestJson {
  implicit def ToJsonCreateHerokuAppBody: ToJson[models.CreateHerokuAppBody]
  implicit def ToJsonUpdateHerokuAppBody: ToJson[models.UpdateHerokuAppBody]
  implicit def ToJsonHerokuAppStack: ToJson[models.HerokuAppStack]
  implicit def ToJsonHerokuAppRegion: ToJson[models.HerokuAppRegion]
  implicit def ToJsonHerokuAppOwner: ToJson[models.HerokuAppOwner]
}

trait HerokuAppResponseJson {
  implicit def FromJsonHerokuAppStack: FromJson[models.HerokuAppStack]
  implicit def FromJsonHerokuAppRegion: FromJson[models.HerokuAppRegion]
  implicit def FromJsonHerokuAppOwner: FromJson[models.HerokuAppOwner]
  implicit def FromJsonHerokuApp: FromJson[HerokuApp]
  implicit def FromJsonListHerokuApp: FromJson[collection.immutable.List[HerokuApp]]
}