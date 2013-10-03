package com.heroku.platform.api

abstract class PlanSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AddonServiceRequestJson with AddonServiceResponseJson with PlanRequestJson with PlanResponseJson = aj

  import implicits._

  "Api for Plans" must {
    "operate on Plans" in {
      val svcs = listAll(AddonService.List())
      val svc = svcs(0)
      val plans = listAll(Plan.List(svc.id))
      val plan = plans(0)
      val pinfo = info(Plan.Info(svc.name, plan.id))
      pinfo must equal(plan)
    }
  }

}

