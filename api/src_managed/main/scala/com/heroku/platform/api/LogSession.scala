package com.heroku.platform.api

import com.heroku.platform.api.Request._

import LogSession._

object LogSession {
  import LogSession.models._
  object models {
    case class CreateLogSessionBody(dyno: Option[String] = None, lines: Option[Int] = None, source: Option[String] = None, tail: Option[Boolean] = None)
  }
  case class Create(app_id_or_name: String, dyno: Option[String] = None, lines: Option[Int] = None, source: Option[String] = None, tail: Option[Boolean] = None) extends RequestWithBody[models.CreateLogSessionBody, LogSession] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/log-sessions".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateLogSessionBody = models.CreateLogSessionBody(dyno, lines, source, tail)
  }
}

case class LogSession(created_at: String, id: String, logplex_url: String, updated_at: String)

trait LogSessionRequestJson {
  implicit def ToJsonCreateLogSessionBody: ToJson[models.CreateLogSessionBody]
}

trait LogSessionResponseJson {
  implicit def FromJsonLogSession: FromJson[LogSession]
  implicit def FromJsonListLogSession: FromJson[collection.immutable.List[LogSession]]
}