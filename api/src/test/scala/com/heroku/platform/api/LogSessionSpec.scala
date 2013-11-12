package com.heroku.platform.api

import scala.concurrent.duration._
import scala.concurrent.Await

abstract class LogSessionSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: LogSessionRequestJson with LogSessionResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for LogSessions" must {
    "operate on LogSessions" in {
      import primary._
      val app = getApp
      trySession(app) must equal(true)
    }
  }

  def trySession(app: HerokuApp, tries: Int = 10): Boolean = {
    if (tries == 0) false
    else {
      Await.result(api.execute(LogSession.Create(app.id), primaryTestApiKey), 5 seconds) match {
        case Left(Response(_, _, ErrorResponse(id, msg))) if msg.startsWith("Logplex was just enabled for this app") =>
          println("Logplex was just enabled for this app")
          Thread.sleep(1000)
          trySession(app, tries - 1)
        case Left(_) =>
          println("Unexpected Error Creating Log Session")
          false
        case Right(_) => true
      }
    }
  }

}

