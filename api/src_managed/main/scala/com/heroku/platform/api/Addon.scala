package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Addon._

/** Add-ons represent add-ons that have been provisioned for an app. */
object Addon {
  import Addon.models._
  object models {
    case class CreateAddonBody(config: Option[Map[String, String]] = None, plan: String)
    case class UpdateAddonBody(plan: String)
    case class AddonPlan(id: String, name: String)
  }
  /** Create a new add-on. */
  case class Create(app_id_or_name: String, config: Option[Map[String, String]] = None, plan_id_or_name: String) extends RequestWithBody[models.CreateAddonBody, Addon] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/addons".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateAddonBody = models.CreateAddonBody(config, plan_id_or_name)
  }
  /** Delete an existing add-on. */
  case class Delete(app_id_or_name: String, addon_id_or_name: String) extends Request[Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/addons/%s".format(app_id_or_name, addon_id_or_name)
    val method: String = DELETE
  }
  /** Info for an existing add-on. */
  case class Info(app_id_or_name: String, addon_id_or_name: String) extends Request[Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/addons/%s".format(app_id_or_name, addon_id_or_name)
    val method: String = GET
  }
  /** List existing add-ons. */
  case class List(app_id_or_name: String, range: Option[String] = None) extends ListRequest[Addon] {
    val endpoint: String = "/apps/%s/addons".format(app_id_or_name)
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Addon] = this.copy(range = Some(nextRange))
  }
  /** Update an existing add-on. */
  case class Update(app_id_or_name: String, addon_id_or_name: String, plan_id_or_name: String) extends RequestWithBody[models.UpdateAddonBody, Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/addons/%s".format(app_id_or_name, addon_id_or_name)
    val method: String = PATCH
    val body: models.UpdateAddonBody = models.UpdateAddonBody(plan_id_or_name)
  }
}

/** Add-ons represent add-ons that have been provisioned for an app. */
case class Addon(name: String, provider_id: String, id: String, created_at: String, updated_at: String, plan: models.AddonPlan)

/** json serializers related to Addon */
trait AddonRequestJson {
  implicit def ToJsonCreateAddonBody: ToJson[models.CreateAddonBody]
  implicit def ToJsonUpdateAddonBody: ToJson[models.UpdateAddonBody]
  implicit def ToJsonAddonPlan: ToJson[models.AddonPlan]
}

/** json deserializers related to Addon */
trait AddonResponseJson {
  implicit def FromJsonAddonPlan: FromJson[models.AddonPlan]
  implicit def FromJsonAddon: FromJson[Addon]
  implicit def FromJsonListAddon: FromJson[collection.immutable.List[Addon]]
}