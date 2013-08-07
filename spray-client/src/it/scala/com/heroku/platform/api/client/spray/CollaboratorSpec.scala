package com.heroku.platform.api.client.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._
import com.heroku.platform.api.client.spray.SprayApi._

class CollaboratorSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Collaborator" must {
    "operate on Collaborators" in {
      val app = getApp
      val collabAdd = create(Collaborator.Create(app.id, testCollaborator))
      val collabList = listPage(Collaborator.List(app.id))
      collabList.isComplete must be(true)
      collabList.list.isEmpty must be(false)
      collabList.list.contains(collabAdd) must be(true)
      val collabInfo = info(Collaborator.Info(app.id, collabAdd.id))
      collabInfo must equal(collabAdd)
      delete(Collaborator.Delete(app.id, collabAdd.id))
    }
  }

}
