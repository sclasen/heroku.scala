package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Stack._

/** Stacks are the different application execution environment available in the Heroku platform. */
object Stack {
  import Stack.models._
  object models {
    ()
  }
  /** Stack info. */
  case class Info(stack_name_or_id: String) extends Request[Stack] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/stacks/%s".format(stack_name_or_id)
    val method: String = GET
  }
  /** List available stacks. */
  case class List(range: Option[String] = None) extends ListRequest[Stack] {
    val endpoint: String = "/stacks"
    val method: String = GET
    def nextRequest(nextRange: String): ListRequest[Stack] = this.copy(range = Some(nextRange))
  }
}

/** Stacks are the different application execution environment available in the Heroku platform. */
case class Stack(name: String, state: String, id: String, created_at: String, updated_at: String)

/** json deserializers related to Stack */
trait StackResponseJson {
  implicit def FromJsonStack: FromJson[Stack]
  implicit def FromJsonListStack: FromJson[collection.immutable.List[Stack]]
}