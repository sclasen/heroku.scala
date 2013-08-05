package com.heroku.api

import com.heroku.api.Request._

case class Region(created_at: String, description: String, id: String, name: String, updated_at: String)

case class RegionInfo(id: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Region] {
  val endpoint = s"/regions/$id"
  val expect = expect200
  val method = GET
}

case class RegionList(range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Region] {
  val endpoint = "/regions"
  val method = GET

  def nextRequest(nextRange: String): ListRequest[Region] = this.copy(range = Some(nextRange))
}

trait RegionResponseJson {
  implicit def regionListFromJson: FromJson[List[Region]]

  implicit def regionFromJson: FromJson[Region]
}