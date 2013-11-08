package com.heroku.platform.api

abstract class CollaboratorSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: CollaboratorRequestJson with CollaboratorResponseJson = aj

  import implicits._

  "Api for Collaborator" must {
    "operate on Collaborators" in {
      val app = getApp
      val collabAdd = execute(Collaborator.Create(app.id, user_email_or_id = testCollaborator))
      val collabList = listPage(Collaborator.List(app.id))
      collabList.isComplete must be(true)
      collabList.list.isEmpty must be(false)
      collabList.list.contains(collabAdd) must be(true)
      val collabInfo = execute(Collaborator.Info(app.id, collabAdd.id))
      collabInfo must equal(collabAdd)
      execute(Collaborator.Delete(app.id, collabAdd.id))
    }
  }

}

