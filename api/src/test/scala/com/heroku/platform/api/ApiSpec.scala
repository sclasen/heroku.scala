package com.heroku.platform.api

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import org.scalatest.{ BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.ListBuffer
import org.scalatest.exceptions.TestFailedException

abstract class ApiSpec(val aj: ApiRequestJson with ApiResponseJson) extends WordSpec with BeforeAndAfterAll with MustMatchers {

  def api: Api

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

  def execute[I, T](rwb: RequestWithBody[I, T])(implicit t: ToJson[I], f: FromJson[T]): T = loggingFailure(rwb) {
    await(api.execute(rwb, apiKey))
  }

  def execute[T](req: Request[T])(implicit f: FromJson[T]): T = loggingFailure(req) {
    await(api.execute(req, apiKey))
  }

  def listAll[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): List[T] = loggingFailure(list) {
    await(api.executeListAll(list, apiKey))
  }

  def listPage[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): PartialResponse[T] = loggingFailure(list) {
    await(api.executeList(list, apiKey))
  }

  def getApp = {
    val app = createApp
    apps += app
    app
  }

  override protected def afterAll() {
    implicit val ex = concurrent.ExecutionContext.Implicits.global
    Await.ready(
      Future.sequence {
        apps.map {
          app => destroyApp(app)
        }
      }, 5.seconds)
    shutdown
  }

  def createApp: HerokuApp

  def destroyApp(app: HerokuApp): Future[Either[ErrorResponse, HerokuApp]]

  def shutdown: Unit
}
