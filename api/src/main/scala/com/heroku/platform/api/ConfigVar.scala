package com.heroku.platform.api
import Request._

object ConfigVar {

  case class Info(appId: String) extends Request[Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/config-vars"
    val method: String = GET
  }

  case class Update(appId: String, configVarsToSet: Map[String, String] = Map.empty, configVarsToRemove: Set[String] = Set.empty) extends RequestWithBody[Map[String, String], Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/config-vars"
    val method: String = PATCH
    val body = {
      configVarsToSet ++ (configVarsToRemove.map(rem => rem -> null).toMap[String, String])
    }
  }

}

trait ConfigVarResponseJson {
  implicit def configFromJson: FromJson[Map[String, String]]
}

trait ConfigVarRequestJson {
  implicit def configToJson: ToJson[Map[String, String]]
}