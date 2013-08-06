package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class CollaboratorSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Collaborator" must {
    "operate on Collaborators" in {
      val app = getApp
      val collabList = listPage(Collaborator.List(app.id))
      collabList.isComplete must be(true)
      collabList.list.isEmpty must be(false)
      val collab = collabList.list.head
      val collabInfo = info(Collaborator.Info(app.id, collab.id))
      collabInfo must equal(collab)
    }
  }

}
