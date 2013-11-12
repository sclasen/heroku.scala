package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Formation._

/** The formation of processes that should be maintained for an app. Update the formation to scale processes or change dyno sizes. Commands and types are defined by the Procfile uploaded with an app. */
object Formation {
  import Formation.models._
  object models {
    case class UpdateFormationBody(quantity: Option[Int] = None, size: Option[String] = None)
  }
  /** Info for a process type */
  case class Info(app_id_or_name: String, formation_id_or_type: String) extends Request[Formation] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/formation/%s".format(app_id_or_name, formation_id_or_type)
    val method: String = GET
  }
  /** List process type formation */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Formation] {
    val endpoint: String = "/apps/%s/formation".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Formation] = this.copy(range = Some(nextRange))
  }
  /** Update process type */
  case class Update(app_id_or_name: String, formation_id_or_type: String, quantity: Option[Int] = None, size: Option[String] = None) extends RequestWithBody[models.UpdateFormationBody, Formation] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/formation/%s".format(app_id_or_name, formation_id_or_type)
    val method: String = PATCH
    val body: models.UpdateFormationBody = models.UpdateFormationBody(quantity, size)
  }
}

/** The formation of processes that should be maintained for an app. Update the formation to scale processes or change dyno sizes. Commands and types are defined by the Procfile uploaded with an app. */
case class Formation(quantity: Int, size: String, command: String, id: String, created_at: String, `type`: String, updated_at: String)

/** json serializers related to Formation */
trait FormationRequestJson {
  implicit def ToJsonUpdateFormationBody: ToJson[models.UpdateFormationBody]
}

/** json deserializers related to Formation */
trait FormationResponseJson {
  implicit def FromJsonFormation: FromJson[Formation]
  implicit def FromJsonListFormation: FromJson[collection.immutable.List[Formation]]
}