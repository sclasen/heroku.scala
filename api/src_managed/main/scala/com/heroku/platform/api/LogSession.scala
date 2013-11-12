package com.heroku.platform.api

import com.heroku.platform.api.Request._

import LogSession._

/** A log session is a reference to the http based log stream for an app. */
object LogSession {
  import LogSession.models._
  object models {
    case class CreateLogSessionBody(dyno: Option[String] = None, lines: Option[Int] = None, source: Option[String] = None, tail: Option[Boolean] = None)
  }
  /** Create a new log session. */
  case class Create(app_id_or_name: String, dyno: Option[String] = None, lines: Option[Int] = None, source: Option[String] = None, tail: Option[Boolean] = None) extends RequestWithBody[models.CreateLogSessionBody, LogSession] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/log-sessions".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateLogSessionBody = models.CreateLogSessionBody(dyno, lines, source, tail)
  }
}

/** A log session is a reference to the http based log stream for an app. */
case class LogSession(created_at: String, id: String, logplex_url: String, updated_at: String)

/** json serializers related to LogSession */
trait LogSessionRequestJson {
  implicit def ToJsonCreateLogSessionBody: ToJson[models.CreateLogSessionBody]
}

/** json deserializers related to LogSession */
trait LogSessionResponseJson {
  implicit def FromJsonLogSession: FromJson[LogSession]
  implicit def FromJsonListLogSession: FromJson[collection.immutable.List[LogSession]]
}