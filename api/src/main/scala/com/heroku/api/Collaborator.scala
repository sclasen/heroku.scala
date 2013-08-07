package com.heroku.api

import Request._
import com.heroku.api.Collaborator.{ CollaboratorBody, CollaboratedUser }

object Collaborator {
  case class CollaboratedUser(email: String, id: String)
  case class CollaboratorBody(user: UserBody)

  case class Create(appId: String, email: String, extraHeaders: Map[String, String] = Map.empty) extends RequestWithBody[CollaboratorBody, Collaborator] {
    val expect: Set[Int] = expect201
    val endpoint: String = s"/apps/$appId/collaborators"
    val method: String = POST
    val body: CollaboratorBody = CollaboratorBody(UserBody(email = Some(email)))
  }

  case class List(appId: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Collaborator] {
    val endpoint: String = s"/apps/$appId/collaborators"
    val method: String = GET

    def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
  }

  case class Info(appId: String, collaboratorId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Collaborator] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/collaborators/$collaboratorId"
    val method: String = GET
  }

  case class Delete(appId: String, collaboratorId: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Collaborator] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/collaborators/$collaboratorId"
    val method: String = DELETE
  }

}

case class Collaborator(created_at: String, id: String, user: CollaboratedUser)

trait CollaboratorResponseJson {
  implicit def collaboratorFromJson: FromJson[Collaborator]
  implicit def collaboratorListFromJson: FromJson[List[Collaborator]]
  implicit def collaboratedUserFromJson: FromJson[CollaboratedUser]
}

trait CollaboratorRequestJson {
  implicit def collaboratorBodyToJson: ToJson[CollaboratorBody]
}