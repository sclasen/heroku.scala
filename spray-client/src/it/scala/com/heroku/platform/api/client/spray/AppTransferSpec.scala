package com.heroku.platform.api.client.spray


import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._
import SprayApiJson._


class AppTransferSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for App Transfers" must {
    "operate on AppTransfers" in {
      val app = getApp
      import AppTransfer._
      create(Collaborator.Create(app.id, testCollaborator))
      val transfer = create(Create(App(id = Some(app.id)),UserBody(email = Some(testCollaborator))))
      val transferList = listAll(List())
      transferList.contains(transfer) must be(true)
      val transferInfo = info(Info(transfer.id))
      transferInfo must equal(transfer)
      //TODO TEST UPDATE to accept/decline
      delete(Delete(transfer.id))
    }
  }

}

