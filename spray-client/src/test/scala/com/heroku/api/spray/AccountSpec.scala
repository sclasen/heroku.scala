package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import akka.actor.ActorSystem
import com.heroku.api._
import com.heroku.api.CreateAppBody
import com.heroku.api.AppUpdate
import com.heroku.api.AppList
import scala.Some
import com.heroku.api.AppCreate


class AccountSpec extends WordSpec with MustMatchers {


  "Spray Api implementation of Account operations" must {
    "compile" in {
      import SprayApi._
      val system = ActorSystem("test")
      val api = new SprayApi(system)
      val key = "foo"
      val update = AccountUpdate(UpdateAccount(allow_tracking = Some(false)))
      api.execute(update, key)
      val info = AccountInfo()
      api.execute(info, key)
    }
  }

}
