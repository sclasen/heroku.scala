package com.heroku.platform.api

import com.heroku.platform.api.Request._

import LogDrain._

/** [Log drains](https://devcenter.heroku.com/articles/logging#syslog-drains) provide a way to forward your Heroku logs to an external syslog server for long-term archiving. This external service must be configured to receive syslog packets from Heroku, whereupon its URL can be added to an app using this API. */
object LogDrain {
  import LogDrain.models._
  object models {
    case class CreateLogDrainBody(url: String)
  }
  /** Create a new log drain. */
  case class Create(app_id_or_name: String, url: String) extends RequestWithBody[models.CreateLogDrainBody, LogDrain] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/log-drains".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateLogDrainBody = models.CreateLogDrainBody(url)
  }
  /** Delete an existing log drain. */
  case class Delete(app_id_or_name: String, log_drain_id_or_url: String) extends Request[LogDrain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/log-drains/%s".format(app_id_or_name, log_drain_id_or_url)
    val method: String = DELETE
  }
  /** Info for existing log drain. */
  case class Info(app_id_or_name: String, log_drain_id_or_url: String) extends Request[LogDrain] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/log-drains/%s".format(app_id_or_name, log_drain_id_or_url)
    val method: String = GET
  }
  /** List existing log drains. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[LogDrain] {
    val endpoint: String = "/apps/%s/log-drains".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[LogDrain] = this.copy(range = Some(nextRange))
  }
}

/** [Log drains](https://devcenter.heroku.com/articles/logging#syslog-drains) provide a way to forward your Heroku logs to an external syslog server for long-term archiving. This external service must be configured to receive syslog packets from Heroku, whereupon its URL can be added to an app using this API. */
case class LogDrain(url: String, addon: Option[String], id: String, created_at: String, updated_at: String)

/** json serializers related to LogDrain */
trait LogDrainRequestJson {
  implicit def ToJsonCreateLogDrainBody: ToJson[models.CreateLogDrainBody]
}

/** json deserializers related to LogDrain */
trait LogDrainResponseJson {
  implicit def FromJsonLogDrain: FromJson[LogDrain]
  implicit def FromJsonListLogDrain: FromJson[collection.immutable.List[LogDrain]]
}