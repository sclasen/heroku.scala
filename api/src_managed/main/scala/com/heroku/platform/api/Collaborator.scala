package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Collaborator._

object Collaborator {
  import Collaborator.models._
  object models {
    case class CreateCollaboratorBody(silent: Option[Boolean] = None, user: String)
    case class CollaboratorUser(email: String, id: String)
  }
  case class Create(app_id_or_name: String, silent: Option[Boolean] = None, user_email_or_id: String) extends RequestWithBody[models.CreateCollaboratorBody, Collaborator] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/collaborators".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateCollaboratorBody = models.CreateCollaboratorBody(silent, user_email_or_id)
  }
  case class Delete(app_id_or_name: String, collaborator_email_or_id: String) extends Request[Collaborator] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/collaborators/%s".format(app_id_or_name, collaborator_email_or_id)
    val method: String = DELETE
  }
  case class Info(app_id_or_name: String, collaborator_email_or_id: String) extends Request[Collaborator] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/collaborators/%s".format(app_id_or_name, collaborator_email_or_id)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Collaborator] {
    val endpoint: String = "/apps/%s/collaborators".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Collaborator] = this.copy(range = Some(nextRange))
  }
}

case class Collaborator(created_at: String, id: String, updated_at: String, user: models.CollaboratorUser)

trait CollaboratorRequestJson {
  implicit def ToJsonCreateCollaboratorBody: ToJson[models.CreateCollaboratorBody]
  implicit def ToJsonCollaboratorUser: ToJson[models.CollaboratorUser]
}

trait CollaboratorResponseJson {
  implicit def FromJsonCollaboratorUser: FromJson[models.CollaboratorUser]
  implicit def FromJsonCollaborator: FromJson[Collaborator]
  implicit def FromJsonListCollaborator: FromJson[collection.immutable.List[Collaborator]]
}