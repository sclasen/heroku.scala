package com.heroku.platform.api

import Request._

case class Release(created_at: String, description: String, id: String, version: Long, updated_at: String, user: User)

object Release {

  case class Info(app_id_or_name: String, release_id_or_name: String, extraHeaders: Map[String, String] = Map.empty) extends Request[Release] {
    val endpoint = s"/apps/$app_id_or_name/releases/$release_id_or_name"
    val expect = expect200
    val method = GET
  }

  case class List(app_id_or_name: String, range: Option[String] = None, extraHeaders: Map[String, String] = Map.empty) extends ListRequest[Release] {
    val endpoint = s"/apps/$app_id_or_name/releases"
    val method = GET

    def nextRequest(nextRange: String): ListRequest[Release] = this.copy(range = Some(nextRange))
  }

}

trait ReleaseResponseJson {
  implicit def releaseFromJson: FromJson[Release]

  implicit def releaseListFromJson: FromJson[List[Release]]
}

