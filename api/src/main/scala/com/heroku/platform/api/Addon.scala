package com.heroku.platform.api

import Request._
import com.heroku.platform.api.Addon.models.{ AddonChange, AddonPlan }

case class Addon(
  config: Option[Map[String, String]],
  created_at: String,
  id: String,
  plan: AddonPlan,
  updated_at: String)

object Addon {

  object models {

    case class AddonPlan(name: String)

    case class AddonChange(config: Option[Map[String, String]], plan: AddonPlan)

  }

  case class Create(appIdOrName: String, plan: String, config: Map[String, String] = Map.empty, headers: Map[String, String] = Map.empty) extends RequestWithBody[AddonChange, Addon] {
    val expect: Set[Int] = expect201
    val endpoint: String = s"/apps/$appIdOrName/addons"
    val method: String = POST
    val body = AddonChange(if (config.isEmpty) None else Some(config), AddonPlan(plan))
  }

  case class Info(appIdOrName: String, addonIdOrName: String, headers: Map[String, String] = Map.empty) extends Request[Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appIdOrName/addons/$addonIdOrName"
    val method: String = GET
  }

  case class List(appIdOrName: String, range: Option[String] = None, headers: Map[String, String] = Map.empty) extends ListRequest[Addon] {
    def nextRequest(nextRange: String): ListRequest[Addon] = this.copy(range = Some(nextRange))

    val endpoint: String = s"/apps/$appIdOrName/addons"
    val method: String = GET
  }

  case class Update(appIdOrName: String, addonIdOrName: String, plan: String, config: Map[String, String] = Map.empty, headers: Map[String, String] = Map.empty) extends RequestWithBody[AddonChange, Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appIdOrName/addons/$addonIdOrName"
    val method: String = PATCH
    val body = AddonChange(if (config.isEmpty) None else Some(config), AddonPlan(plan))
  }

  case class Delete(appIdOrName: String, addonIdOrName: String, headers: Map[String, String] = Map.empty) extends Request[Addon] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appIdOrName/addons/$addonIdOrName"
    val method: String = DELETE
  }

}

trait AddonRequestJson {
  implicit def addonChangeToJson: ToJson[AddonChange]

  implicit def addonPlanToJson: ToJson[AddonPlan]
}

trait AddonResponseJson {
  implicit def addonPlanFromJson: FromJson[AddonPlan]

  implicit def addonFromJson: FromJson[Addon]

  implicit def addonListFromJson: FromJson[List[Addon]]
}

