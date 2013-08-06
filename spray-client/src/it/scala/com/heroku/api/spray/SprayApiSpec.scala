package com.heroku.api.spray

import akka.actor.ActorSystem
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.heroku.api._
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.MustMatchers
import SprayApi._
import scala.collection.mutable.ListBuffer
import com.heroku.api.ErrorResponse

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

  def create[I,T](rwb: RequestWithBody[I, T])(implicit t:ToJson[I], f:FromJson[T]): T = {
    await(api.execute(rwb, apiKey))
  }

  def info[T](req: Request[T])(implicit f:FromJson[T]): T = {
    await(api.execute(req, apiKey))
  }

  def update[I,T](rwb: RequestWithBody[I, T])(implicit t:ToJson[I], f:FromJson[T]): T = {
    await(api.execute(rwb, apiKey))
  }

  def listAll[T](list:ListRequest[T])(implicit f:FromJson[List[T]]):List[T] = {
    await(api.executeListAll(list,apiKey))
  }

  def listPage[T](list:ListRequest[T])(implicit f:FromJson[List[T]]):PartialResponse[T] = {
    await(api.executeList(list,apiKey))
  }

  def delete[T](del:Request[T])(implicit f:FromJson[T]):T={
    await(api.execute(del, apiKey))
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
