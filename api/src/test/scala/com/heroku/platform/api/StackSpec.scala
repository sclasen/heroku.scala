package com.heroku.platform.api

abstract class StackSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: StackRequestJson with StackResponseJson = aj

  import implicits._

  "Api for Stacks" must {
    "operate on Stacks" in {
      val stacks = listAll(Stack.List())
      val stack = stacks(0)
      val byId = execute(Stack.Info(stack.id))
      val byName = execute(Stack.Info(stack.name))
      stack must equal(byId)
      stack must equal(byName)
    }

  }

}

