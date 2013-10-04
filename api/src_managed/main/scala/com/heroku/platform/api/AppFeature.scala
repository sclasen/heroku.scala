package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AppFeature._

object AppFeature {
  import AppFeature.models._
  object models {
    case class UpdateAppFeatureBody(enabled: Boolean)
  }
  case class Info(app_id_or_name: String, app_feature_id_or_name: String) extends Request[AppFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/features/%s".format(app_id_or_name, app_feature_id_or_name)
    val method: String = GET
  }
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[AppFeature] {
    val endpoint: String = "/apps/%s/features".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AppFeature] = this.copy(range = Some(nextRange))
  }
  case class Update(app_id_or_name: String, app_feature_id_or_name: String, enabled: Boolean) extends RequestWithBody[models.UpdateAppFeatureBody, AppFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/features/%s".format(app_id_or_name, app_feature_id_or_name)
    val method: String = PATCH
    val body: models.UpdateAppFeatureBody = models.UpdateAppFeatureBody(enabled)
  }
}

case class AppFeature(name: String, description: String, enabled: Boolean, id: String, doc_url: String, created_at: String, updated_at: String)

case class AppFeatureIdentity(id: Option[String], name: Option[String])

case object AppFeatureIdentity {
  def byId(id: String) = AppFeatureIdentity(Some(id), None)
  def byName(name: String) = AppFeatureIdentity(None, Some(name))
}

trait AppFeatureRequestJson {
  implicit def ToJsonUpdateAppFeatureBody: ToJson[models.UpdateAppFeatureBody]
  implicit def ToJsonAppFeatureIdentity: ToJson[AppFeatureIdentity]
}

trait AppFeatureResponseJson {
  implicit def FromJsonAppFeature: FromJson[AppFeature]
  implicit def FromJsonListAppFeature: FromJson[collection.immutable.List[AppFeature]]
}