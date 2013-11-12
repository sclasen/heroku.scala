package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Region._

/** A region represents a geographic location in which your application may run. */
object Region {
  import Region.models._
  object models {
    ()
  }
  /** Info for existing region. */
  case class Info(region_id_or_name: String) extends Request[Region] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/regions/%s".format(region_id_or_name)
    val method: String = GET
  }
  /** List existing regions. */
  case class List(range: Option[String] = None) extends ListRequest[Region] {
    val endpoint: String = "/regions"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Region] = this.copy(range = Some(nextRange))
  }
}

/** A region represents a geographic location in which your application may run. */
case class Region(name: String, description: String, id: String, created_at: String, updated_at: String)

/** json deserializers related to Region */
trait RegionResponseJson {
  implicit def FromJsonRegion: FromJson[Region]
  implicit def FromJsonListRegion: FromJson[collection.immutable.List[Region]]
}