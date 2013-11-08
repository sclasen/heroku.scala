package com.heroku.platform.api

import com.heroku.platform.api.Collaborator.models.CollaboratorUser
import com.heroku.platform.api.AppTransfer.models.{ AppTransferRecipient, AppTransferApp }

abstract class AppTransferSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AppTransferRequestJson with AppTransferResponseJson with CollaboratorRequestJson with CollaboratorResponseJson = aj

  import implicits._

  "Spray Api for App Transfers" must {
    "operate on AppTransfers" in {
      val app = getApp
      import AppTransfer._
      execute(Collaborator.Create(app.id, user_email_or_id = testCollaborator))
      val transfer = execute(Create(app.id, recipient_email_or_id = testCollaborator))
      val transferList = listAll(List())
      transferList.contains(transfer) must be(true)
      val transferInfo = execute(Info(transfer.id))
      transferInfo must equal(transfer)
      //TODO TEST UPDATE to accept/decline
      execute(Delete(transfer.id))
    }
  }

}

