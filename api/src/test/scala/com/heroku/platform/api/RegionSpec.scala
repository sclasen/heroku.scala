package com.heroku.platform.api

abstract class RegionSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: RegionRequestJson with RegionResponseJson = aj

  import implicits._

  "Api for Regions" must {
    "operate on Regions" in {
      import primary._
      val regions = requestAll(Region.List())
      val region = regions(0)
      val byId = request(Region.Info(region.id))
      val byName = request(Region.Info(region.name))
      region must equal(byId)
      region must equal(byName)
    }
  }

}

