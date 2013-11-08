package com.heroku.platform.api

abstract class AddonServiceSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonServiceRequestJson with AddonServiceResponseJson = aj

  import implicits._

  "Api for AddonServices" must {
    "operate on AddonServices" in {
      import primary._
      val services = requestAll(AddonService.List())
      val serviceByName = request(AddonService.Info(services(0).name))
      val serviceById = request(AddonService.Info(services(0).id))
      serviceByName must equal(serviceById)
    }
  }

}

