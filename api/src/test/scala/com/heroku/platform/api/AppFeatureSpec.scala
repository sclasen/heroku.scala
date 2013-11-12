package com.heroku.platform.api

abstract class AppFeatureSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AppFeatureRequestJson with AppFeatureResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for AppFeature" must {
    "operate on AppFeatures" in {
      import primary._
      val app = getApp
      val features = requestAll(AppFeature.List(app.id))
      val feature = request(AppFeature.Info(app.name, features(0).id))
      feature must equal(features(0))
      val updated = request(AppFeature.Update(app.id, feature.id, !feature.enabled))
      updated.enabled must equal(!feature.enabled)
    }
  }

}

