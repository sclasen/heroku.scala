package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class AppSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Apps" must {
    "operate on Apps" in {
      val appList = await(api.executeListAll(HerokuApp.List(), apiKey))

      val app = appList.head

      val infoByName = await(api.execute(HerokuApp.Info(app.name), apiKey))
      infoByName must equal(app)

      val infoById = await(api.execute(HerokuApp.Info(app.id), apiKey))
      infoById must equal(app)
    }
  }

}
