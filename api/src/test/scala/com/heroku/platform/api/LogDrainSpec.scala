package com.heroku.platform.api

import scala.concurrent._
import scala.concurrent.duration._

abstract class LogDrainSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: LogDrainRequestJson with LogDrainResponseJson = aj

  import implicits._

  "Api for LogDrains" must {
    "operate on LogDrains" in {
      val app = getApp
      tryDrain(app, "https://example.com/foo") must be(true)
      val drains = listAll(LogDrain.List(app.name))
      val created = drains(0)
      val drainByid = info(LogDrain.Info(app.id, created.id))
      drainByid must equal(created)
      //todo deal with urlencoding urls
      //val drainByUrl = info(LogDrain.Info(app.id, "https://example.com/foo"))
      //drainByUrl must equal(created)
      val deleted = delete(LogDrain.Delete(app.id, created.id))
    }
  }

  def tryDrain(app: HerokuApp, drain: String, tries: Int = 10): Boolean = {
    if (tries == 0) false
    else {
      Await.result(api.execute(LogDrain.Create(app.id, drain), apiKey), 5 seconds) match {
        case Left(ErrorResponse(id, msg)) if msg.startsWith("Could not list logplex drains. Please try again later.") =>
          println("Could not list logplex drains. Please try again later.")
          Thread.sleep(1000)
          tryDrain(app, drain, tries - 1)
        case Left(_) =>
          println("Unexpected Error Creating Log Drain")
          false
        case Right(_) => true
      }
    }
  }

}

