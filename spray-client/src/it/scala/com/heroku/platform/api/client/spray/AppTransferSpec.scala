package com.heroku.platform.api.client.spray


import com.heroku.platform.api._


abstract class AppTransferSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AppTransferRequestJson with AppTransferResponseJson with CollaboratorRequestJson with CollaboratorResponseJson = aj

  import implicits._


  "Spray Api for App Transfers" must {
    "operate on AppTransfers" in {
      val app = getApp
      import AppTransfer._
      create(Collaborator.Create(app.id, testCollaborator))
      val transfer = create(Create(App(id = Some(app.id)), UserBody(email = Some(testCollaborator))))
      val transferList = listAll(List())
      transferList.contains(transfer) must be(true)
      val transferInfo = info(Info(transfer.id))
      transferInfo must equal(transfer)
      //TODO TEST UPDATE to accept/decline
      delete(Delete(transfer.id))
    }
  }

}

