package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AccountFeature._

/** An account feature represents a Heroku labs capability that can be enabled or disabled for an account on Heroku. */
object AccountFeature {
  import AccountFeature.models._
  object models {
    case class UpdateAccountFeatureBody(enabled: Boolean)
  }
  /** Info for an existing account feature. */
  case class Info(account_feature_id_or_name: String) extends Request[AccountFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/features/%s".format(account_feature_id_or_name)
    val method: String = GET
  }
  /** List existing account features. */
  case class List(range: Option[String] = None) extends ListRequest[AccountFeature] {
    val endpoint: String = "/account/features"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AccountFeature] = this.copy(range = Some(nextRange))
  }
  /** Update an existing account feature. */
  case class Update(account_feature_id_or_name: String, enabled: Boolean) extends RequestWithBody[models.UpdateAccountFeatureBody, AccountFeature] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/features/%s".format(account_feature_id_or_name)
    val method: String = PATCH
    val body: models.UpdateAccountFeatureBody = models.UpdateAccountFeatureBody(enabled)
  }
}

/** An account feature represents a Heroku labs capability that can be enabled or disabled for an account on Heroku. */
case class AccountFeature(name: String, description: String, enabled: Boolean, id: String, doc_url: String, created_at: String, updated_at: String)

/** json serializers related to AccountFeature */
trait AccountFeatureRequestJson {
  implicit def ToJsonUpdateAccountFeatureBody: ToJson[models.UpdateAccountFeatureBody]
}

/** json deserializers related to AccountFeature */
trait AccountFeatureResponseJson {
  implicit def FromJsonAccountFeature: FromJson[AccountFeature]
  implicit def FromJsonListAccountFeature: FromJson[collection.immutable.List[AccountFeature]]
}