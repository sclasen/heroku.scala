package com.heroku.platform.api.examples

import com.heroku.platform.api.client.spray.SprayKeySpec
import com.heroku.platform.api.{ErrorResponseJson, KeyResponseJson, KeyRequestJson}


class JacksonKeySpec extends SprayKeySpec{
  override val implicits: KeyRequestJson with KeyResponseJson with ErrorResponseJson = JacksonJson
}