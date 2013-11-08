package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Release._

object Release {
  import Release.models._
  object models {
    case class RollbackReleaseBody(release: String)
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
  case class Rollback(app_id_or_name: String, release: String) extends RequestWithBody[models.RollbackReleaseBody, Release] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = POST
    val body: models.RollbackReleaseBody = models.RollbackReleaseBody(release)
  }
}

case class Release(description: String, version: Int, id: String, created_at: String, updated_at: String, user: models.ReleaseUser)

trait ReleaseRequestJson {
  implicit def ToJsonRollbackReleaseBody: ToJson[models.RollbackReleaseBody]
  implicit def ToJsonReleaseUser: ToJson[models.ReleaseUser]
}

trait ReleaseResponseJson {
  implicit def FromJsonReleaseUser: FromJson[models.ReleaseUser]
  implicit def FromJsonRelease: FromJson[Release]
  implicit def FromJsonListRelease: FromJson[collection.immutable.List[Release]]
}