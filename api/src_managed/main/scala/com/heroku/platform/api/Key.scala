package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Key._

/** Keys represent public SSH keys associated with an account and are used to authorize accounts as they are performing git operations. */
object Key {
  import Key.models._
  object models {
    case class CreateKeyBody(public_key: String)
  }
  /** Create a new key. */
  case class Create(public_key: String) extends RequestWithBody[models.CreateKeyBody, Key] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/account/keys"
    val method: String = POST
    val body: models.CreateKeyBody = models.CreateKeyBody(public_key)
  }
  /** Delete an existing key */
  case class Delete(key_id_or_fingerprint: String) extends Request[Key] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/keys/%s".format(key_id_or_fingerprint)
    val method: String = DELETE
  }
  /** Info for existing key. */
  case class Info(key_id_or_fingerprint: String) extends Request[Key] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/account/keys/%s".format(key_id_or_fingerprint)
    val method: String = GET
  }
  /** List existing keys. */
  case class List(range: Option[String] = None) extends ListRequest[Key] {
    val endpoint: String = "/account/keys"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Key] = this.copy(range = Some(nextRange))
  }
}

/** Keys represent public SSH keys associated with an account and are used to authorize accounts as they are performing git operations. */
case class Key(fingerprint: String, email: String, public_key: String, id: String, created_at: String, updated_at: String)

/** json serializers related to Key */
trait KeyRequestJson {
  implicit def ToJsonCreateKeyBody: ToJson[models.CreateKeyBody]
}

/** json deserializers related to Key */
trait KeyResponseJson {
  implicit def FromJsonKey: FromJson[Key]
  implicit def FromJsonListKey: FromJson[collection.immutable.List[Key]]
}