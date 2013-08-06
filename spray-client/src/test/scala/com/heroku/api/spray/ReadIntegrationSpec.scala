package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._
import akka.actor._
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class ReadIntegrationSpec extends WordSpec with MustMatchers {

  val system = ActorSystem("api")

  val api = new SprayApi(system)

  val apiKey = sys.env("TEST_API_KEY")

  "SprayApi" must {
    "read account info" in {
      val info = await(api.execute(Account.Info(), apiKey))
      info.isRight must be(true)
    }

    "read app info" in {
      val appList = await(api.executeListAll(HerokuApp.List(), apiKey))
      appList.isRight must be(true)

      appList.right.get.isEmpty must be(false)
      val app = appList.right.get.head

      val infoByName = await(api.execute(HerokuApp.Info(app.name), apiKey))
      infoByName.isRight must be(true)
      infoByName.right.get must equal(app)

      val infoById = await(api.execute(HerokuApp.Info(app.id), apiKey))
      infoById.isRight must be(true)
      infoById.right.get must equal(app)
    }

    "read collaborators" in {
      val app = getApp
      val collabList = await(api.executeList(Collaborator.List(app.id), apiKey))
      collabList.isRight must be(true)
      collabList.right.get.isComplete must be(true)
      collabList.right.get.list.isEmpty must be(false)
      val collab = collabList.right.get.list.head
      val collabInfo = await(api.execute(Collaborator.Info(app.id, collab.id), apiKey))
      collabInfo.isRight must be(true)
      collabInfo.right.get must equal(collab)
    }

    "read config vars" in {
      val app = getApp
      val vars = await(api.execute(ConfigVar.Info(app.id), apiKey))
      vars.isRight must be(true)
    }
  }

  def await[T](future: Future[T]): T = {
    Await.result(future, 5.seconds)
  }

  def getApp = await(api.executeList(HerokuApp.List(), apiKey)).right.get.list.head

}
