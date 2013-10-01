package com.heroku.platform.api

abstract class AppFeatureSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AppFeatureRequestJson with AppFeatureResponseJson = aj

  import implicits._

  "Api for AppFeature" must {
    "operate on AppFeatures" in {
      val app = getApp
      val features = listAll(AppFeature.List(app.id))
      val feature = info(AppFeature.Info(app.name, features(0).id))
      feature must equal(features(0))
      val updated = update(AppFeature.Update(app.id, feature.id, !feature.enabled))
      updated.enabled must equal(!feature.enabled)
    }
  }

}

