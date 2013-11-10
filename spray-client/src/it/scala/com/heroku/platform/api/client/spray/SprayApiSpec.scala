package com.heroku.platform.api.client.spray

import scala.concurrent.Future
import com.heroku.platform.api.{ApiSpec, HerokuApp, ErrorResponse}
import akka.actor.ActorSystem
import com.heroku.platform.api.Api.FutureResponse


trait SprayApiSpec {
  this: ApiSpec =>

  val system = ActorSystem("test")

  val api = new SprayApi(system)

  def createApp: HerokuApp = {
    import aj._
    await(api.execute(HerokuApp.Create(), primaryTestApiKey))
  }

  def destroyApp(app: HerokuApp): FutureResponse[HerokuApp] = {
    import aj._
    api.execute(HerokuApp.Delete(app.id), primaryTestApiKey)
  }

  def shutdown {
    println("shutting down api actor system")
    system.shutdown
  }
}
