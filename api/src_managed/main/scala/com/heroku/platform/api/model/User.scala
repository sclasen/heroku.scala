package com.heroku.platform.api.model

import com.heroku.platform.api._

import com.heroku.platform.api.Request._

import User._

object User {
  import User.models._
  object models {
    ()
  }
}

case class User

trait UserRequestJson {
  ()
}

trait UserResponseJson {
  implicit def FromJsonUser: FromJson[User]
  implicit def FromJsonListUser: FromJson[collection.immutable.List[User]]
}