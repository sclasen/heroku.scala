package com.heroku.platform.api.client.spray


import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.platform.api._


abstract class AddonSpec extends WordSpec with SprayApiSpec with MustMatchers {

  val addonImplicits:AddonRequestJson with AddonResponseJson

  import addonImplicits._

  "SprayApi for Addons" must {
    "operate on the Addons" in {
      val app = getApp
      val addon = create(Addon.Create(app.id ,"scheduler:standard"))
      val addonList = listAll(Addon.List(app.id))
      addonList.contains(addon) must be(true)
      val addonInfo = info(Addon.Info(app.id, addon.id))
      addonInfo must equal(addon)
      delete(Addon.Delete(app.id, addon.id))
    }
  }

}

class SprayAddonSpec extends AddonSpec{
  val addonImplicits: AddonRequestJson with AddonResponseJson = SprayJsonBoilerplate
}


