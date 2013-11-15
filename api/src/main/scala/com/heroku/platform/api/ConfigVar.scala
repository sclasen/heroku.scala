package com.heroku.platform.api

import Request._

object ConfigVar {

  object models {
    case class Update(name: String, value: String)
    case class MultiUpdateConfigVarBody(`config-vars`: Seq[Update])
  }

  case class Info(app_id_or_name: String) extends Request[Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$app_id_or_name/config-vars"
    val method: String = GET
  }

  case class MultiUpdate(app_id_or_name: String, `config-vars`: Seq[models.Update]) extends RequestWithBody[models.MultiUpdateConfigVarBody, Map[String, String]] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$app_id_or_name/config-vars"
    val method: String = PATCH
    val body = models.MultiUpdateConfigVarBody(`config-vars`)
  }

}

trait ConfigVarResponseJson {
  implicit def FromJsonConfigVar: FromJson[Map[String, String]]
}

trait ConfigVarRequestJson {
  implicit def ToJsonConfigVar: ToJson[Map[String, String]]
}