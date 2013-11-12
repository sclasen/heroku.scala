package com.heroku.platform.api

abstract class CollaboratorSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: CollaboratorRequestJson with CollaboratorResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Collaborator" must {
    "operate on Collaborators" in {
      import primary._
      val app = getApp
      val collabAdd = request(Collaborator.Create(app.id, user_email_or_id = secondaryTestUser))
      val collabList = requestPage(Collaborator.List(app.id))
      collabList.isComplete must be(true)
      collabList.list.isEmpty must be(false)
      collabList.list.contains(collabAdd) must be(true)
      val collabInfo = request(Collaborator.Info(app.id, collabAdd.id))
      collabInfo must equal(collabAdd)
      request(Collaborator.Delete(app.id, collabAdd.id))
    }
  }

}

