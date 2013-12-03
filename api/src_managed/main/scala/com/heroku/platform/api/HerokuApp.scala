package com.heroku.platform.api

import com.heroku.platform.api.Request._

import HerokuApp._

/** An app represents the program that you would like to deploy and run on Heroku. */
object HerokuApp {
  import HerokuApp.models._
  object models {
    case class CreateHerokuAppBody(name: Option[String] = None, region: Option[String] = None, stack: Option[String] = None)
    case class UpdateHerokuAppBody(maintenance: Option[Boolean] = None, name: Option[String] = None)
    case class HerokuAppRegion(id: String, name: String)
    case class HerokuAppOwner(email: String, id: String)
  }
  /** Create a new app. */
  case class Create(name: Option[String] = None, region_id_or_name: Option[String] = None, stack_name_or_id: Option[String] = None) extends RequestWithBody[models.CreateHerokuAppBody, HerokuApp] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps"
    val method: String = POST
    val body: models.CreateHerokuAppBody = models.CreateHerokuAppBody(name, region_id_or_name, stack_name_or_id)
  }
  /** Delete an existing app. */
  case class Delete(app_id_or_name: String) extends Request[HerokuApp] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s".format(app_id_or_name)
    val method: String = DELETE
  }
  /** Info for existing app. */
  case class Info(app_id_or_name: String) extends Request[HerokuApp] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s".format(app_id_or_name)
    val method: String = GET
  }
  /** List existing apps. */
  case class List(range: Option[String] = None) extends ListRequest[HerokuApp] {
    val endpoint: String = "/apps"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[HerokuApp] = this.copy(range = Some(nextRange))
  }
  /** Update an existing app. */
  case class Update(app_id_or_name: String, maintenance: Option[Boolean] = None, name: Option[String] = None) extends RequestWithBody[models.UpdateHerokuAppBody, HerokuApp] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s".format(app_id_or_name)
    val method: String = PATCH
    val body: models.UpdateHerokuAppBody = models.UpdateHerokuAppBody(maintenance, name)
  }
}

/** An app represents the program that you would like to deploy and run on Heroku. */
case class HerokuApp(name: String, repo_size: Option[Int], git_url: String, slug_size: Option[Int], maintenance: Boolean, id: String, released_at: Option[String], web_url: String, stack: String, region: models.HerokuAppRegion, created_at: String, owner: models.HerokuAppOwner, updated_at: String, archived_at: Option[String], buildpack_provided_description: Option[String])

/** json serializers related to HerokuApp */
trait HerokuAppRequestJson {
  implicit def ToJsonCreateHerokuAppBody: ToJson[models.CreateHerokuAppBody]
  implicit def ToJsonUpdateHerokuAppBody: ToJson[models.UpdateHerokuAppBody]
  implicit def ToJsonHerokuAppRegion: ToJson[models.HerokuAppRegion]
  implicit def ToJsonHerokuAppOwner: ToJson[models.HerokuAppOwner]
}

/** json deserializers related to HerokuApp */
trait HerokuAppResponseJson {
  implicit def FromJsonHerokuAppRegion: FromJson[models.HerokuAppRegion]
  implicit def FromJsonHerokuAppOwner: FromJson[models.HerokuAppOwner]
  implicit def FromJsonHerokuApp: FromJson[HerokuApp]
  implicit def FromJsonListHerokuApp: FromJson[collection.immutable.List[HerokuApp]]
}