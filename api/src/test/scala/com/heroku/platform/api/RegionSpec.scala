package com.heroku.platform.api

abstract class RegionSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: RegionRequestJson with RegionResponseJson = aj

  import implicits._

  "Api for Regions" must {
    "operate on Regions" in {
      val regions = listAll(Region.List())
      val region = regions(0)
      val byId = execute(Region.Info(region.id))
      val byName = execute(Region.Info(region.name))
      region must equal(byId)
      region must equal(byName)
    }
  }

}

