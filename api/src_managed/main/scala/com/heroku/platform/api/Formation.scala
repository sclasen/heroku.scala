package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Formation._

object Formation {
  import Formation.models._
  object models {
    case class UpdateFormationBody(quantity: Option[Int] = None, size: Option[Int] = None)
  }
  case class Info(app_id_or_name: String, formation_id_or_type: String) extends Request[Formation] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/formation/%s".format(app_id_or_name, formation_id_or_type)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Formation] {
    val endpoint: String = "/apps/%s/formation".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Formation] = this.copy(range = Some(nextRange))
  }
  case class Update(app_id_or_name: String, formation_id_or_type: String, quantity: Option[Int] = None, size: Option[Int] = None) extends RequestWithBody[models.UpdateFormationBody, Formation] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/formation/%s".format(app_id_or_name, formation_id_or_type)
    val method: String = PATCH
    val body: models.UpdateFormationBody = models.UpdateFormationBody(quantity, size)
  }
}

case class Formation(quantity: Int, size: Int, command: String, id: String, created_at: String, `type`: String, updated_at: String)

trait FormationRequestJson {
  implicit def ToJsonUpdateFormationBody: ToJson[models.UpdateFormationBody]
}

trait FormationResponseJson {
  implicit def FromJsonFormation: FromJson[Formation]
  implicit def FromJsonListFormation: FromJson[collection.immutable.List[Formation]]
}