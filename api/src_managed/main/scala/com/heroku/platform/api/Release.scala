package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Release._

object Release {
  import Release.models._
  object models {
    case class RollbackReleaseBody(id: String)
    case class ReleaseUser(id: String, email: String)
  }
  case class Info(app_id_or_name: String, release_id_or_version: String) extends Request[Release] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/releases/%s".format(app_id_or_name, release_id_or_version)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Release] {
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Release] = this.copy(range = Some(nextRange))
  }
  case class Rollback(app_id_or_name: String, id: String) extends RequestWithBody[models.RollbackReleaseBody, Release] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = POST
    val body: models.RollbackReleaseBody = models.RollbackReleaseBody(id)
  }
}

case class Release(description: String, version: Int, id: String, created_at: String, updated_at: String, user: models.ReleaseUser)

case class ReleaseIdentity(id: Option[String], version: Option[String])

case object ReleaseIdentity {
  def byId(id: String) = ReleaseIdentity(Some(id), None)
  def byVersion(version: String) = ReleaseIdentity(None, Some(version))
}

trait ReleaseRequestJson {
  implicit def ToJsonRollbackReleaseBody: ToJson[models.RollbackReleaseBody]
  implicit def ToJsonReleaseUser: ToJson[models.ReleaseUser]
  implicit def ToJsonReleaseIdentity: ToJson[ReleaseIdentity]
}

trait ReleaseResponseJson {
  implicit def FromJsonReleaseUser: FromJson[models.ReleaseUser]
  implicit def FromJsonRelease: FromJson[Release]
  implicit def FromJsonListRelease: FromJson[collection.immutable.List[Release]]
}