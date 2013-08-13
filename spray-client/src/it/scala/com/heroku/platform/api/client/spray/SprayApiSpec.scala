package com.heroku.platform.api.client.spray

import scala.concurrent.Future
import com.heroku.platform.api.{HerokuApp, ErrorResponse}
import akka.actor.ActorSystem


trait SprayApiSpec {
  this: ApiSpec =>

  val system = ActorSystem("test")

  val api = new SprayApi(system)(aj)

  def createApp: HerokuApp = {
    import aj._
    await(api.execute(HerokuApp.Create(), apiKey))
  }

  def destroyApp(app: HerokuApp): Future[Either[ErrorResponse, HerokuApp]] = {
    import aj._
    api.execute(HerokuApp.Delete(app.id), apiKey)
  }

  def shutdown {
    println("shutting down api actor system")
    system.shutdown
  }
}
