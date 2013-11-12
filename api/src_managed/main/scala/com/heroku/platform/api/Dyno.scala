package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Dyno._

/** Dynos encapsulate running processes of an app on Heroku. */
object Dyno {
  import Dyno.models._
  object models {
    case class CreateDynoBody(attach: Option[Boolean] = None, command: String, env: Option[Map[String, String]] = None, size: Option[String] = None)
    case class DynoRelease(id: String, version: Int)
  }
  /** Create a new dyno. */
  case class Create(app_id_or_name: String, attach: Option[Boolean] = None, command: String, env: Option[Map[String, String]] = None, size: Option[String] = None) extends RequestWithBody[models.CreateDynoBody, Dyno] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/dynos".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateDynoBody = models.CreateDynoBody(attach, command, env, size)
  }
  /** Restart dyno. */
  case class RestartDyno(app_id_or_name: String, dyno_id_or_name: String) extends RequestWithEmptyResponse {
    val expect: Set[Int] = expect202
    val endpoint: String = "/apps/%s/dynos/%s".format(app_id_or_name, dyno_id_or_name)
    val method: String = DELETE
  }
  /** Restart all dynos */
  case class RestartAllDynos(app_id_or_name: String) extends RequestWithEmptyResponse {
    val expect: Set[Int] = expect202
    val endpoint: String = "/apps/%s/dynos".format(app_id_or_name)
    val method: String = DELETE
  }
  /** Info for existing dyno. */
  case class Info(app_id_or_name: String, dyno_id_or_name: String) extends Request[Dyno] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/dynos/%s".format(app_id_or_name, dyno_id_or_name)
    val method: String = GET
  }
  /** List existing dynos. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Dyno] {
    val endpoint: String = "/apps/%s/dynos".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Dyno] = this.copy(range = Some(nextRange))
  }
}

/** Dynos encapsulate running processes of an app on Heroku. */
case class Dyno(name: String, size: String, state: String, attach_url: Option[String], command: String, id: String, created_at: String, release: models.DynoRelease, `type`: String, updated_at: String)

/** json serializers related to Dyno */
trait DynoRequestJson {
  implicit def ToJsonCreateDynoBody: ToJson[models.CreateDynoBody]
  implicit def ToJsonDynoRelease: ToJson[models.DynoRelease]
}

/** json deserializers related to Dyno */
trait DynoResponseJson {
  implicit def FromJsonDynoRelease: FromJson[models.DynoRelease]
  implicit def FromJsonDyno: FromJson[Dyno]
  implicit def FromJsonListDyno: FromJson[collection.immutable.List[Dyno]]
}