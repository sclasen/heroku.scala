package com.heroku.platform.api.model

import com.heroku.platform.api._

import com.heroku.platform.api.Request._

import Stack._

object Stack {
  import Stack.models._
  object models {
    ()
  }
}

case class Stack

trait StackRequestJson {
  ()
}

trait StackResponseJson {
  implicit def FromJsonStack: FromJson[Stack]
  implicit def FromJsonListStack: FromJson[collection.immutable.List[Stack]]
}