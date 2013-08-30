package com.heroku.platform.api

import com.heroku.platform.api.Request._
import com.heroku.platform.api.Dyno.models.{ CreateDynoBody, DynoRelease }

case class Dyno(attach_url: Option[String], command: String, created_at: String, id: String, name: String, release: DynoRelease, state: String, `type`: String, updated_at: String)

object Dyno {

  object models {
    //STUFF HERE INSTEAD OF COMPANION SINCE THAT BREAKS PLAY JSON

    case class CreateDynoBody(command: String, attach: Boolean = false)

    case class DynoRelease(id: String)
  }

  case class Create(appId: String, command: String, attach: Boolean = false, headers: Map[String, String] = Map.empty) extends RequestWithBody[CreateDynoBody, Dyno] {
    val expect: Set[Int] = expect201
    val endpoint: String = s"/apps/$appId/dynos"
    val method: String = POST
    val body = CreateDynoBody(command, attach)
  }

  case class List(appId: String, range: Option[String] = None, headers: Map[String, String] = Map.empty) extends ListRequest[Dyno] {
    val endpoint: String = s"/apps/$appId/dynos"
    val method: String = GET

    def nextRequest(nextRange: String) = this.copy(range = Some(nextRange))
  }

  case class Info(appId: String, dynoId: String, headers: Map[String, String] = Map.empty) extends Request[Dyno] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/dynos/$dynoId"
    val method: String = GET
  }

  case class Delete(appId: String, dynoId: String, headers: Map[String, String] = Map.empty) extends Request[Dyno] {
    val expect: Set[Int] = expect200
    val endpoint: String = s"/apps/$appId/dynos/$dynoId"
    val method: String = DELETE
  }
}

trait DynoResponseJson {
  implicit def dynoReleaseFromJson: FromJson[DynoRelease]
  implicit def dynoFromJson: FromJson[Dyno]
}

trait DynoRequestJson {
  implicit def createDynoBodyToJson: ToJson[CreateDynoBody]
}

