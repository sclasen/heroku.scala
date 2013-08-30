package com.heroku.platform.api.client.spray

import akka.actor.ActorSystem

import scala.{ App => SApp }
import com.heroku.platform.api.model._
import concurrent.ExecutionContext.Implicits.global
import com.heroku.platform.api.{ ErrorResponse, FromJson, ErrorResponseJson }
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created with IntelliJ IDEA.
 * User: scott
 * Date: 8/26/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */

object TestMain extends SApp {

  def apiKey: String = sys.env("TEST_API_KEY")

  import SprayJsonBoilerplate._

  val syst = ActorSystem()

  val api = new SprayApi(syst)(SprayJsonBoilerplate)

  {
    val result: Either[ErrorResponse, HerokuApp] = Await.result(api.execute(HerokuApp.Info("6757d8ee-dbca-4979-b051-a9e0d9e51afc"), apiKey), 10 seconds)
    result.fold(e => println(s"ERROR$e"), s => println(s"SUCCESS$s"))
  }

}