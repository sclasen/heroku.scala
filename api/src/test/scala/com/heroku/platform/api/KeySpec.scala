package com.heroku.platform.api

import scala.io.Source

abstract class KeySpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: KeyRequestJson with KeyResponseJson = aj

  import implicits._

  "Api for Keys" must {
    "operate on Keys" in {
      val key = Source.fromFile("api/src/test/resources/test_key.pub").getLines().foldLeft(new StringBuilder)(_.append(_)).toString
      val created = execute(Key.Create(key))
      val keys = listAll(Key.List())
      keys(0) must equal(created)
      val keyInfo = execute(Key.Info(created.fingerprint))
      keyInfo must equal(created)
      val deleted = execute(Key.Delete(created.id))
    }
  }

}

