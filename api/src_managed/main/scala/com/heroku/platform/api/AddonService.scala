package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AddonService._

object AddonService {
  import AddonService.models._
  object models {
    ()
  }
  case class Info(addon_service_id_or_name: String) extends Request[AddonService] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/addon-services/%s".format(addon_service_id_or_name)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[AddonService] {
    val endpoint: String = "/addon-services"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AddonService] = this.copy(range = Some(nextRange))
  }
}

case class AddonService(created_at: String, id: String, name: String, updated_at: String)

case class AddonServiceIdentity(id: Option[String], name: Option[String])

case object AddonServiceIdentity {
  def byId(id: String) = AddonServiceIdentity(Some(id), None)
  def byName(name: String) = AddonServiceIdentity(None, Some(name))
}

trait AddonServiceRequestJson {
  implicit def ToJsonAddonServiceIdentity: ToJson[AddonServiceIdentity]
}

trait AddonServiceResponseJson {
  implicit def FromJsonAddonService: FromJson[AddonService]
  implicit def FromJsonListAddonService: FromJson[collection.immutable.List[AddonService]]
}