package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Release._

/** A release represents a combination of code, config vars and add-ons for an app on Heroku. */
object Release {
  import Release.models._
  object models {
    case class CreateReleaseBody(description: Option[String] = None, slug: String)
    case class RollbackReleaseBody(release: String)
    case class ReleaseSlug(id: String)
    case class ReleaseUser(id: String, email: String)
  }
  /** Info for existing release. */
  case class Info(app_id_or_name: String, release_id_or_version: String) extends Request[Release] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/releases/%s".format(app_id_or_name, release_id_or_version)
    val method: String = GET
  }
  /** List existing releases. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Release] {
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Release] = this.copy(range = Some(nextRange))
  }
  /** Create new release. */
  case class Create(app_id_or_name: String, description: Option[String] = None, slug_id: String) extends RequestWithBody[models.CreateReleaseBody, Release] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateReleaseBody = models.CreateReleaseBody(description, slug_id)
  }
  /** Rollback to an existing release. */
  case class Rollback(app_id_or_name: String, release: String) extends RequestWithBody[models.RollbackReleaseBody, Release] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/releases".format(app_id_or_name)
    val method: String = POST
    val body: models.RollbackReleaseBody = models.RollbackReleaseBody(release)
  }
}

/** A release represents a combination of code, config vars and add-ons for an app on Heroku. */
case class Release(description: String, slug: Option[models.ReleaseSlug], version: Int, id: String, created_at: String, updated_at: String, user: models.ReleaseUser)

/** json serializers related to Release */
trait ReleaseRequestJson {
  implicit def ToJsonCreateReleaseBody: ToJson[models.CreateReleaseBody]
  implicit def ToJsonRollbackReleaseBody: ToJson[models.RollbackReleaseBody]
  implicit def ToJsonReleaseSlug: ToJson[models.ReleaseSlug]
  implicit def ToJsonReleaseUser: ToJson[models.ReleaseUser]
}

/** json deserializers related to Release */
trait ReleaseResponseJson {
  implicit def FromJsonReleaseSlug: FromJson[models.ReleaseSlug]
  implicit def FromJsonReleaseUser: FromJson[models.ReleaseUser]
  implicit def FromJsonRelease: FromJson[Release]
  implicit def FromJsonListRelease: FromJson[collection.immutable.List[Release]]
}