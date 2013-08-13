package com.heroku.platform.api

abstract class AddonSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonRequestJson with AddonResponseJson = aj

  import implicits._

  "Api for Addons" must {
    "operate on the Addons" in {
      val app = getApp
      val addon = create(Addon.Create(app.id, "scheduler:standard"))
      val addonList = listAll(Addon.List(app.id))
      addonList.contains(addon) must be(true)
      val addonInfo = info(Addon.Info(app.id, addon.id))
      addonInfo must equal(addon)
      delete(Addon.Delete(app.id, addon.id))
    }
  }

}

