package com.heroku.platform.api

import Request._

object ConfigVar {

  case class Info(app_id_or_name: String) extends Request[Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$app_id_or_name/config-vars"
    val method: String = GET
  }

  case class Update(app_id_or_name: String, configVarsToSet: Map[String, String] = Map.empty, configVarsToRemove: Set[String] = Set.empty) extends RequestWithBody[Map[String, String], Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$app_id_or_name/config-vars"
    val method: String = PATCH
    val body = {
      configVarsToSet ++ (configVarsToRemove.map(rem => rem -> null).toMap[String, String])
    }
  }

}

trait ConfigVarResponseJson {
  implicit def FromJsonConfigVar: FromJson[Map[String, String]]
}

trait ConfigVarRequestJson {
  implicit def ToJsonConfigVar: ToJson[Map[String, String]]
}