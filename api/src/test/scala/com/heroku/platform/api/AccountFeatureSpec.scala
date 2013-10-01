package com.heroku.platform.api

abstract class AccountFeatureSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: AccountFeatureRequestJson with AccountFeatureResponseJson = aj

  import implicits._

  "Api for AccountFeature" must {
    "operate on AccountFeatures" in {
      val features = listAll(AccountFeature.List())
      val feature = info(AccountFeature.Info(features(0).id))
      feature must equal(features(0))
      val updated = update(AccountFeature.Update(feature.id, !feature.enabled))
      updated.enabled must equal(!feature.enabled)
    }
  }

}
