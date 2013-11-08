package com.heroku.platform.api

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import org.scalatest.{ BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.ListBuffer
import org.scalatest.exceptions.TestFailedException

abstract class ApiSpec(val aj: ApiRequestJson with ApiResponseJson) extends WordSpec with BeforeAndAfterAll with MustMatchers {

  def api: Api

  def primaryTestApiKey = sys.env("TEST_API_KEY_1")

  def primaryTestUser = sys.env("TEST_USER_1")

  def primaryTestPassword = sys.env("TEST_PASSWORD_1")

  def secondaryTestApiKey = sys.env("TEST_API_KEY_2")

  def secondaryTestUser = sys.env("TEST_USER_2")

  def secondaryTestPassword = sys.env("TEST_PASSWORD_2")

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

  val primary = ApiWrapper(primaryTestApiKey)

  val secondary = ApiWrapper(secondaryTestApiKey)

  case class ApiWrapper(apiKey: String) {

    def request[I, T](rwb: RequestWithBody[I, T])(implicit t: ToJson[I], f: FromJson[T]): T = loggingFailure(rwb) {
      await(api.execute(rwb, apiKey))
    }

    def request[T](req: Request[T])(implicit f: FromJson[T]): T = loggingFailure(req) {
      await(api.execute(req, apiKey))
    }

    def requestAll[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): List[T] = loggingFailure(list) {
      await(api.executeListAll(list, apiKey))
    }

    def requestPage[T](list: ListRequest[T])(implicit f: FromJson[List[T]]): PartialResponse[T] = loggingFailure(list) {
      await(api.executeList(list, apiKey))
    }

    def getApp = {
      val app = createApp
      apps += app
      app
    }

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
