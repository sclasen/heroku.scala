package com.heroku.platform.api

import com.heroku.platform.api.Request._

import AddonService._

/** Add-on services represent add-ons that may be provisioned for apps. */
object AddonService {
  import AddonService.models._
  object models {
    ()
  }
  /** Info for existing addon-service. */
  case class Info(addon_service_id_or_name: String) extends Request[AddonService] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/addon-services/%s".format(addon_service_id_or_name)
    val method: String = GET
  }
  /** List existing addon-services. */
  case class List(range: Option[String] = None) extends ListRequest[AddonService] {
    val endpoint: String = "/addon-services"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[AddonService] = this.copy(range = Some(nextRange))
  }
}

/** Add-on services represent add-ons that may be provisioned for apps. */
case class AddonService(created_at: String, id: String, name: String, updated_at: String)

/** json deserializers related to AddonService */
trait AddonServiceResponseJson {
  implicit def FromJsonAddonService: FromJson[AddonService]
  implicit def FromJsonListAddonService: FromJson[collection.immutable.List[AddonService]]
}