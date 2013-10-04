package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Stack._

object Stack {
  import Stack.models._
  object models {
    ()
  }
  case class Info(stack_name_or_id: String) extends Request[Stack] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/stacks/%s".format(stack_name_or_id)
    val method: String = GET
  }
  case class List(range: Option[String] = None) extends ListRequest[Stack] {
    val endpoint: String = "/stacks"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Stack] = this.copy(range = Some(nextRange))
  }
}

case class Stack(name: String, state: String, id: String, created_at: String, updated_at: String)

case class StackIdentity(name: Option[String], id: Option[String])

case object StackIdentity {
  def byName(name: String) = StackIdentity(Some(name), None)
  def byId(id: String) = StackIdentity(None, Some(id))
}

trait StackRequestJson {
  implicit def ToJsonStackIdentity: ToJson[StackIdentity]
}

trait StackResponseJson {
  implicit def FromJsonStack: FromJson[Stack]
  implicit def FromJsonListStack: FromJson[collection.immutable.List[Stack]]
}