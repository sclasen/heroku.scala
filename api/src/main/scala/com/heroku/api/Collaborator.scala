package com.heroku.api

import Request._

case class CollaboratedUser(email: String, id: String)

case class Collaborator(created_at: String, id: String, user: CollaboratedUser)

case class CollaboratorBody(email: String)

case class CollaboratorCreate(appId: String, email: String, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CollaboratorBody, Collaborator] {
  val expect: Set[Int] = expect201
  val endpoint: String = s"/apps/$appId/collaborators"
  val method: String = POST
  val body: CollaboratorBody = CollaboratorBody(email)
}

case class CollaboratorList(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Collaborator] {
  val endpoint: String = s"/apps/$appId/collaborators"
  val method: String = GET

  def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
}

case class CollaboratorInfo(appId: String, collaboratorId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Collaborator] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/collaborators"
  val method: String = GET
}

case class CollaboratorDelete(appId: String, collaboratorId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Collaborator] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appId/collaborators"
  val method: String = DELETE
}

trait CollaboratorJson{
  implicit def collaboratorFromJson:FromJson[Collaborator]
  implicit def collaboratedUserFromJson:FromJson[CollaboratedUser]
  implicit def collaboratorBodyToJson:ToJson[CollaboratorBody]
}