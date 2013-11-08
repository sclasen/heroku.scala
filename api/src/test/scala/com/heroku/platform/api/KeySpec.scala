package com.heroku.platform.api

import scala.io.Source

abstract class KeySpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: KeyRequestJson with KeyResponseJson = aj

  import implicits._

  "Api for Keys" must {
    "operate on Keys" in {
      import primary._
      val key = Source.fromFile("api/src/test/resources/test_key.pub").getLines().foldLeft(new StringBuilder)(_.append(_)).toString
      val created = request(Key.Create(key))
      val keys = requestAll(Key.List())
      keys(0) must equal(created)
      val keyInfo = request(Key.Info(created.fingerprint))
      keyInfo must equal(created)
      val deleted = request(Key.Delete(created.id))
    }
  }

}

