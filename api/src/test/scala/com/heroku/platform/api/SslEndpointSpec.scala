package com.heroku.platform.api

abstract class SslEndpointSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: SslEndpointRequestJson with SslEndpointResponseJson with AddonRequestJson with AddonResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for SslEndpoints" must {
    "operate on SslEndpoints" in {
      import primary._
      pending
     /* val app = getApp
      val crt = io.Source.fromFile("api/src/test/resources/ssl-endpoint/ssl-endpoint-server.crt").mkString
      val key = io.Source.fromFile("api/src/test/resources/ssl-endpoint/ssl-endpoint-server.key").mkString
      val addon = request(Addon.Create(app.id, "ssl:endpoint"))
      val created = request(SslEndpoint.Create(app.id, crt, key))
      try {
        val info = request(SslEndpoint.Info(app.id, created.id))
        info.id must equal(created.id)
        val list = requestAll(SslEndpoint.List(app.id))
        list.map(_.id) must contain(created.id)
      } finally {
        val deleted = request(SslEndpoint.Delete(app.id, created.id))
        deleted.id must equal(created.id)
        request(Addon.Delete(app.id, addon.id))
      }*/
    }

  }

}

