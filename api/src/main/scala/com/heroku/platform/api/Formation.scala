package com.heroku.platform.api

import com.heroku.platform.api.Request._
import com.heroku.platform.api.Formation.UpdateFormationBody

object Formation {
  case class UpdateFormationBody(quantity: Int)

  case class ListFormation(appId: String, range: Option[String] = None) extends ListRequest[Formation] {
    val endpoint: String = s"/apps/$appId/formation"
    val method: String = POST

    def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
  }

  case class FormationInfo(appId: String, `type`: String) extends Request[Formation] {
    val endpoint: String = s"/apps/$appId/formation/${`type`}"
    val method: String = GET
    val expect = expect200
  }

  case class UpdateFormation(appId: String, `type`: String, quantity: Int) extends RequestWithBody[UpdateFormationBody, Formation] {
    val endpoint: String = s"/apps/$appId/formation/${`type`}"
    val method: String = PUT
    val expect = expect200
    val body = UpdateFormationBody(quantity)
  }
}

case class Formation(command: String, created_at: String, `type`: String, quantity: Int, updated_at: String)

trait FormationResponseJson {
  implicit def formationFromJson: FromJson[Formation]
  implicit def formationListFromJson: FromJson[List[Formation]]
}

trait FormationRequestJson {
  implicit def updateFormationBodyToJson: ToJson[UpdateFormationBody]
}
