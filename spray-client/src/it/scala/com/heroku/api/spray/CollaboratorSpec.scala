package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class CollaboratorSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Collaborator" must {
    "operate on Collaborators" in {
      val app = getApp
      val collabList = await(api.executeList(Collaborator.List(app.id), apiKey))
      collabList.isComplete must be(true)
      collabList.list.isEmpty must be(false)
      val collab = collabList.list.head
      val collabInfo = await(api.execute(Collaborator.Info(app.id, collab.id), apiKey))
      collabInfo must equal(collab)
    }
  }

}
