package com.heroku.api
import Request._

case class GetConfigVars(appId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Map[String, String]] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/config-vars"
  val method: String = GET
}

case class GetConfigVar(appId: String, configVar: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Map[String, String]] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/config-vars/$configVar"
  val method: String = GET
}

case class UpdateConfigVars(appId: String, configVars: Map[String, String], extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[Map[String, String], Map[String, String]] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/config-vars"
  val method: String = PUT
  val body = configVars
}

trait ConfigVarJson {
  implicit def configToJson: ToJson[Map[String, String]]
  implicit def configFromJson: FromJson[Map[String, String]]
}