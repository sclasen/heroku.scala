package com.heroku.api
import Request._

object ConfigVar {

  case class Info(appId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/config-vars"
    val method: String = GET
  }

  case class Update(appId: String, configVars: Map[String, String], extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[Map[String, String], Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/config-vars"
    val method: String = PATCH
    val body = configVars
  }

}

trait ConfigVarResponseJson {
  implicit def configFromJson: FromJson[Map[String, String]]
}

trait ConfigVarRequestJson {
  implicit def configToJson: ToJson[Map[String, String]]
}