package com.heroku.platform.api

import com.heroku.platform.api.Collaborator.models.CollaboratorUser
import com.heroku.platform.api.AppTransfer.models.{ AppTransferRecipient, AppTransferApp }

abstract class AppTransferSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AppTransferRequestJson with AppTransferResponseJson with CollaboratorRequestJson with CollaboratorResponseJson = aj

  import implicits._

  "Api for App Transfers" must {
    "operate on AppTransfers" in {
      import AppTransfer._
      import primary._
      val app = getApp
      request(Collaborator.Create(app.id, user_email_or_id = secondaryTestUser))
      val toDelete = request(Create(app.id, recipient_email_or_id = secondaryTestUser))
      val transferList = requestAll(List())
      transferList.contains(toDelete) must be(true)
      val transferInfo = request(Info(toDelete.id))
      transferInfo must equal(toDelete)
      request(Delete(toDelete.id))
      val toDecline = request(Create(app.id, recipient_email_or_id = secondaryTestUser))
      val declined = secondary.request(Update(toDecline.id, "declined"))
      declined.id must equal(toDecline.id)
    }
  }

}

