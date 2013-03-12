package com.heroku.api

import Request._

case class LogSession(created_at: String, logplex_url: String)

case class CreateLogSession(appId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[LogSession] {
  val expect = expect200
  val endpoint = s"/apps/$appId/log-sessions"
  val method = POST
}

trait LogSessionJson {
  implicit def logSessionFromJson: FromJson[LogSession]
}
