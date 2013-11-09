package com.heroku.platform.api

abstract class PlanSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonServiceResponseJson with PlanRequestJson with PlanResponseJson = aj

  import implicits._

  "Api for Plans" must {
    "operate on Plans" in {
      import primary._
      val svcs = requestAll(AddonService.List())
      val svc = svcs(0)
      val plans = requestAll(Plan.List(svc.id))
      val plan = plans(0)
      val pinfo = request(Plan.Info(svc.name, plan.id))
      pinfo must equal(plan)
    }
  }

}

