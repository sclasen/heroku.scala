package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class AppSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Apps" must {
    "operate on Apps" in {

      val app = await(api.execute(HerokuApp.Create(), apiKey))

      val appList = await(api.executeListAll(HerokuApp.List(), apiKey))

      appList.contains(app) must be(true)

      val infoByName = await(api.execute(HerokuApp.Info(app.name), apiKey))
      infoByName must equal(app)

      val infoById = await(api.execute(HerokuApp.Info(app.id), apiKey))
      infoById must equal(app)

      val newname = s"${app.name}-foo"
      val updated = await(api.execute(HerokuApp.Update(app.id, Some(true), Some(newname)), apiKey))

      updated.maintenance must be(true)
      updated.name must be(newname)

      await(api.execute(HerokuApp.Delete(updated.id), apiKey))


    }
  }

}
