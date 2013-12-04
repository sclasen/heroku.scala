package com.heroku.platform.api.examples

import com.heroku.platform.api.{KeySpec, HerokuApp, ApiSpec}
import com.heroku.platform.api.Api._
import com.heroku.platform.api.client.spray.SprayJsonBoilerplate

trait FinagleApiSpec {
  this: ApiSpec =>


  val api = new FinagleApi

  def createApp: HerokuApp = {
    import aj._
    await(api.execute(HerokuApp.Create(), primaryTestApiKey))
  }

  def destroyApp(app: HerokuApp): FutureResponse[HerokuApp] = {
    import aj._
    api.execute(HerokuApp.Delete(app.id), primaryTestApiKey)
  }

  def shutdown {
  }
}

class FinagleKeySpec extends KeySpec(SprayJsonBoilerplate) with FinagleApiSpec