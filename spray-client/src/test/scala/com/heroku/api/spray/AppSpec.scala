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
      val create = HerokuApp.Create(stack = Some("cedar"))
      api.execute(create, key)
      val list = HerokuApp.List()
      api.executeList(list, key)
      val update = HerokuApp.Update("some-app", maintenance = Some(false))
      api.execute(update, key)
      val info = HerokuApp.Info("some-app")
      api.execute(info, key)
      val delete = HerokuApp.Delete("some-app")
      api.execute(delete, key)
    }
  }

}
