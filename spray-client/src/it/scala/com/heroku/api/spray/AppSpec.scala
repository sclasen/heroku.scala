package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class AppSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Apps" must {
    "operate on Apps" in {

      val app = create(HerokuApp.Create())

      val appList = listAll(HerokuApp.List())

      appList.contains(app) must be(true)

      val infoByName = info(HerokuApp.Info(app.name))
      infoByName must equal(app)

      val infoById = info(HerokuApp.Info(app.id))
      infoById must equal(app)

      val newname = s"${app.name}-foo"
      val updated = update(HerokuApp.Update(app.id, Some(true), Some(newname)))

      updated.maintenance must be(true)
      updated.name must be(newname)

      delete(HerokuApp.Delete(updated.id))


    }
  }

}
