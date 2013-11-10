package com.heroku.platform.api

abstract class StackSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: StackResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Stacks" must {
    "operate on Stacks" in {
      import primary._
      val stacks = requestAll(Stack.List())
      val stack = stacks(0)
      val byId = request(Stack.Info(stack.id))
      val byName = request(Stack.Info(stack.name))
      stack must equal(byId)
      stack must equal(byName)
    }

  }

}

