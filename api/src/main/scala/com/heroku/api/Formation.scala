package com.heroku.api

import com.heroku.api.Request._

case class Formation(command: String, created_at: String, `type`: String, quantity: Int, updated_at: String)

case class UpdateFormationBody(quantity: Int)

case class ListFormation(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Formation] {
  val endpoint: String = s"/apps/$appId/formation"
  val method: String = POST

  def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
}

case class FormationInfo(appId: String, `type`: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Formation] {
  val endpoint: String = s"/apps/$appId/formation/${`type`}"
  val method: String = GET
  val expect = expect200
}

case class UpdateFormation(appId: String, `type`: String, quantity: Int, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[UpdateFormationBody, Formation] {
  val endpoint: String = s"/apps/$appId/formation/${`type`}"
  val method: String = PUT
  val expect = expect200
  val body = UpdateFormationBody(quantity)
}

trait FormationJson {
  implicit def formationFromJson: FromJson[Formation]
  implicit def formationListFromJson: FromJson[List[Formation]]
  implicit def updateFormationBodyToJson: ToJson[UpdateFormationBody]
}
