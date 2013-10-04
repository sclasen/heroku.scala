package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Region._

object Region {
  import Region.models._
  object models {
    ()
  }
  case class Info(region_id_or_name: String) extends Request[Region] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/regions/%s".format(region_id_or_name)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[Region] {
    val endpoint: String = "/regions"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Region] = this.copy(range = Some(nextRange))
  }
}

case class Region(name: String, description: String, id: String, created_at: String, updated_at: String)

case class RegionIdentity(id: Option[String], name: Option[String])

case object RegionIdentity {
  def byId(id: String) = RegionIdentity(Some(id), None)
  def byName(name: String) = RegionIdentity(None, Some(name))
}

trait RegionRequestJson {
  implicit def ToJsonRegionIdentity: ToJson[RegionIdentity]
}

trait RegionResponseJson {
  implicit def FromJsonRegion: FromJson[Region]
  implicit def FromJsonListRegion: FromJson[collection.immutable.List[Region]]
}