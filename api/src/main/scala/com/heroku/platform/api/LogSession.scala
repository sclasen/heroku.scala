package com.heroku.platform.api

import Request._

case class LogSession(created_at: String, logplex_url: String)

object LogSession {
  case class Create(appId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[LogSession] {
    val expect = expect200
    val endpoint = s"/apps/$appId/log-sessions"
    val method = POST
  }
}

trait LogSessionResponseJson {
  implicit def logSessionFromJson: FromJson[LogSession]
}
