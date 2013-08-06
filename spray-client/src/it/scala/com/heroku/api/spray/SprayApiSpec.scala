package com.heroku.api.spray

import akka.actor.ActorSystem
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.heroku.api.{HerokuApp, ErrorResponse}
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.MustMatchers
import SprayApi._
import scala.collection.mutable.ListBuffer

trait SprayApiSpec extends BeforeAndAfterAll {
  this: WordSpec with MustMatchers =>

  val system = ActorSystem("test")

  val api = new SprayApi(system)

  def apiKey = sys.env("TEST_API_KEY")

  val apps = ListBuffer.empty[HerokuApp]

  def await[T](future: Future[Either[ErrorResponse, T]], d: Duration = 5.seconds): T = {
    val resp = Await.result(future, d)
    resp must be('right)
    resp.right.get
  }

  def getApp = {
    val app = await(api.execute(HerokuApp.Create(), apiKey))
    apps += app
    app
  }


  override protected def afterAll() {
    implicit val ex = system.dispatcher
    Await.ready(
    Future.sequence {
      apps.map {
        app => api.execute(HerokuApp.Delete(app.id), apiKey)
      }
    }, 5.seconds)
    println("shutting down api actor system")
    system.shutdown
  }
}
