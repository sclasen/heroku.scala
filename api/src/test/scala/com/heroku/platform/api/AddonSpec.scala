package com.heroku.platform.api

abstract class AddonSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonRequestJson with AddonResponseJson = aj

  import implicits._

  "Api for Addons" must {
    "operate on the Addons" in {
      import primary._
      val app = getApp
      val addon = request(Addon.Create(app.id, "scheduler:standard"))
      val addonList = requestAll(Addon.List(app.id))
      println(addonList)
      addonList.map(_.id) must contain(addon.id)
      val addonInfo = request(Addon.Info(app.id, addon.id))
      addonInfo must equal(addon)
      request(Addon.Delete(app.id, addon.id))
    }
  }

}

