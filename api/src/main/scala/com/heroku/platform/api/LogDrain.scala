package com.heroku.platform.api

import com.heroku.platform.api.Request._
import LogDrain._

object LogDrain {
  import LogDrain.models._
  object models {
    case class CreateLogDrainBody(url: String, addon: Option[LogDrainAddon])
    case class LogDrainAddon(id: String)
  }
  case class Create(app_id_or_name: String, url: String, addon: Option[models.LogDrainAddon] = None) extends RequestWithBody[models.CreateLogDrainBody, LogDrain] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/log-drains".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateLogDrainBody = models.CreateLogDrainBody(url, addon)
  }
  case class Delete(app_id_or_name: String, log_drain_id_or_url: String) extends Request[LogDrain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/log-drains/%s".format(app_id_or_name, log_drain_id_or_url)
    val method: String = DELETE
  }
  case class Info(app_id_or_name: String, log_drain_id_or_url: String) extends Request[LogDrain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/log-drains/%s".format(app_id_or_name, log_drain_id_or_url)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[LogDrain] {
    val endpoint: String = "/apps/%s/log-drains".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[LogDrain] = this.copy(range = Some(nextRange))
  }
}

case class LogDrain(url: String, addon: Option[models.LogDrainAddon], id: String, created_at: String, updated_at: String)

trait LogDrainRequestJson {
  implicit def ToJsonCreateLogDrainBody: ToJson[models.CreateLogDrainBody]
  implicit def ToJsonLogDrainAddon: ToJson[models.LogDrainAddon]
}

trait LogDrainResponseJson {
  implicit def FromJsonLogDrain: FromJson[LogDrain]
  implicit def FromJsonLogDrainAddon: FromJson[models.LogDrainAddon]
  implicit def FromJsonListLogDrain: FromJson[collection.immutable.List[LogDrain]]
}