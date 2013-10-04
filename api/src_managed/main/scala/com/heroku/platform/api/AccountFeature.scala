package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AccountFeature._

object AccountFeature {
  import AccountFeature.models._
  object models {
    case class UpdateAccountFeatureBody(enabled: Boolean)
  }
  case class Info(account_feature_id_or_name: String) extends Request[AccountFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/features/%s".format(account_feature_id_or_name)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[AccountFeature] {
    val endpoint: String = "/account/features"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AccountFeature] = this.copy(range = Some(nextRange))
  }
  case class Update(account_feature_id_or_name: String, enabled: Boolean) extends RequestWithBody[models.UpdateAccountFeatureBody, AccountFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/features/%s".format(account_feature_id_or_name)
    val method: String = PATCH
    val body: models.UpdateAccountFeatureBody = models.UpdateAccountFeatureBody(enabled)
  }
}

case class AccountFeature(name: String, description: String, enabled: Boolean, id: String, doc_url: String, created_at: String, updated_at: String)

case class AccountFeatureIdentity(id: Option[String], name: Option[String])

case object AccountFeatureIdentity {
  def byId(id: String) = AccountFeatureIdentity(Some(id), None)
  def byName(name: String) = AccountFeatureIdentity(None, Some(name))
}

trait AccountFeatureRequestJson {
  implicit def ToJsonUpdateAccountFeatureBody: ToJson[models.UpdateAccountFeatureBody]
  implicit def ToJsonAccountFeatureIdentity: ToJson[AccountFeatureIdentity]
}

trait AccountFeatureResponseJson {
  implicit def FromJsonAccountFeature: FromJson[AccountFeature]
  implicit def FromJsonListAccountFeature: FromJson[collection.immutable.List[AccountFeature]]
}