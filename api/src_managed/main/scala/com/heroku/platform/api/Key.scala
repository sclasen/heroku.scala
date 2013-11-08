package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Key._

object Key {
  import Key.models._
  object models {
    case class CreateKeyBody(public_key: String)
  }
  case class Create(public_key: String) extends RequestWithBody[models.CreateKeyBody, Key] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/account/keys"
    val method: String = POST
    val body: models.CreateKeyBody = models.CreateKeyBody(public_key)
  }
  case class Delete(key_id_or_fingerprint: String) extends Request[Key] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/keys/%s".format(key_id_or_fingerprint)
    val method: String = DELETE
  }
  case class Info(key_id_or_fingerprint: String) extends Request[Key] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/keys/%s".format(key_id_or_fingerprint)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[Key] {
    val endpoint: String = "/account/keys"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Key] = this.copy(range = Some(nextRange))
  }
}

case class Key(fingerprint: String, email: String, public_key: String, id: String, created_at: String, updated_at: String)

trait KeyRequestJson {
  implicit def ToJsonCreateKeyBody: ToJson[models.CreateKeyBody]
}

trait KeyResponseJson {
  implicit def FromJsonKey: FromJson[Key]
  implicit def FromJsonListKey: FromJson[collection.immutable.List[Key]]
}