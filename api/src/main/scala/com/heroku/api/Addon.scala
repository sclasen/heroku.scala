package com.heroku.api

import Request._
import com.heroku.api.Addon.AddonPlan

case class Addon(
  config: Map[String, String],
  created_at: String,
  id: String,
  plan: AddonPlan,
  updated_at: String)

object Addon {

  case class AddonPlan(name: String)

  case class AddonAdd(config: Map[String, String], plan: AddonAdd)

}

trait AddonRequestJson {
}

trait AddonResponseJson {
  implicit def addonPlanFromJson: FromJson[AddonPlan]

  implicit def addonFromJson: FromJson[Addon]
}

/*case class AddonCreate(appIdOrName:String,) extends RequestWithBody[AddonAdd,Addon]{
  val endpoint: String = s"/apps/$appIdOrName/addons"

}*/

case class AddonInfo(appIdOrName: String, addonIdOrName: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Addon] {
  val expect: Set[Int] = expect200
  val endpoint: String = s"/apps/$appIdOrName/addons/$addonIdOrName"
  val method: String = GET
}

case class AddonList(appIdOrName: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Addon] {
  def nextRequest(nextRange: String): ListRequest[Addon] = this.copy(range = Some(nextRange))

  val endpoint: String = s"/apps/$appIdOrName/addons"
  val method: String = GET
}

