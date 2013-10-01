package com.heroku.platform.api

abstract class AddonServiceSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonServiceRequestJson with AddonServiceResponseJson = aj

  import implicits._

  "Api for AddonServices" must {
    "operate on AddonServices" in {
      val services = listAll(AddonService.List())
      val serviceByName = info(AddonService.Info(services(0).name))
      val serviceById = info(AddonService.Info(services(0).id))
      serviceByName must equal(serviceById)
    }
  }

}

