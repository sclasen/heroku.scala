package com.heroku.platform.api.client.spray


trait SprayApiSpec {
  this: ApiSpec =>

  val api = new SprayApi(system)(aj)

}
