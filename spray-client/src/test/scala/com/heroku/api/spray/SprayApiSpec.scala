package com.heroku.api.spray

import akka.actor.ActorSystem
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import com.heroku.api.ErrorResponse
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

trait SprayApiSpec {
  this: WordSpec with MustMatchers =>

  val system = ActorSystem("test")

  val api = new SprayApi(system)

  def apiKey = sys.env("TEST_API_KEY")

  def await[T](future: Future[Either[ErrorResponse, T]], d: Duration = 5.seconds): T = {
    val resp = Await.result(future, d)
    resp must be('right)
    resp.right.get
  }

}
