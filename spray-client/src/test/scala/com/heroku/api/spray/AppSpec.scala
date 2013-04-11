package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import akka.actor.ActorSystem
import com.heroku.api._

class AppSpec extends WordSpec with MustMatchers {

  "Spray Api implementation of App operations" must {
    "compile" in {
      import SprayApi._
      val system = ActorSystem("test")
      val api = new SprayApi(system)
      val key = "foo"
      val create = AppCreate(stack = Some("cedar"))
      api.execute(create, key)
      val list = AppList()
      api.executeList(list, key)
      val update = AppUpdate("some-app", maintenance = Some(false))
      api.execute(update, key)
      val info = AppInfo("some-app")
      api.execute(info, key)
      val delete = AppDelete("some-app")
      api.execute(delete, key)
    }
  }

}
