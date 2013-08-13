package com.heroku.platform.api.client.spray

import akka.actor.ActorSystem
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import com.heroku.platform.api._
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.ListBuffer
import com.heroku.platform.api.ErrorResponse
import org.scalatest.exceptions.TestFailedException

trait SprayApiSpec extends BeforeAndAfterAll {
  this: WordSpec with MustMatchers =>

  val system = ActorSystem("test")

  val errFrom = new FromJson[ErrorResponse]{
    def fromJson(json: String): ErrorResponse = ErrorResponse("testing", "123")
  }

  val api = new SprayApi(system)(errFrom)

  def apiKey = sys.env("TEST_API_KEY")

  def testCollaborator = sys.env("TEST_COLLABORATOR")

  val apps = ListBuffer.empty[HerokuApp]

  def await[T](future: Future[Either[ErrorResponse, T]], d: Duration = 5.seconds): T = {
    val resp = Await.result(future, d)
    if (resp.isLeft)
      throw new TestFailedException(s"result was not right: ${resp.left.get}", 1)
    else
      resp.right.get
  }

  def loggingFailure[T, U](log: T)(block: => U): U = {
    try {
      block
    } catch {
      case t: TestFailedException =>
        println(s"$log failed")
        throw t
    }
  }

  def create[I, T](rwb: RequestWithBody[I, T])(implicit t: ToJson[I], f: FromJson[T]): T = loggingFailure(rwb) {
    await(api.execute(rwb, apiKey))
  }

  def info[T](req: Request[T])(implicit f: FromJson[T]): T = loggingFailure(req){
    await(api.execute(req, apiKey))
  }

  def update[I, T](rwb: RequestWithBody[I, T])(implicit t: ToJson[I], f: FromJson[T]): T = loggingFailure(rwb){
    await(api.execute(rwb, apiKey))
  }

  def listAll[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): List[T] = loggingFailure(list){
    await(api.executeListAll(list, apiKey))
  }

  def listPage[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): PartialResponse[T] = loggingFailure(list){
    await(api.executeList(list, apiKey))
  }

  def delete[T](del: Request[T])(implicit f: FromJson[T]): T = loggingFailure(del){
    await(api.execute(del, apiKey))
  }


  def getApp = {
    import SprayJsonBoilerplate.createAppBodyToJson
    import SprayJsonBoilerplate.appFromJson
    val app = await(api.execute(HerokuApp.Create(), apiKey))
    apps += app
    app
  }


  override protected def afterAll() {
    implicit val ex = system.dispatcher
    import SprayJsonBoilerplate.appFromJson
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
