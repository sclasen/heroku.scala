package com.heroku.platform.api

abstract class DomainSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: DomainRequestJson with DomainResponseJson = aj

  import implicits._

  "Api for Domains" must {
    "operate on Domains" in {
      import primary._
      val app = getApp
      val domain = request(Domain.Create(app.id, "foo.bar.baz.com"))
      val domainList = requestAll(Domain.List(app.id))
      domainList.contains(domain) must be(true)
      val domainInfo = request(Domain.Info(app.id, domain.id))
      domainInfo must equal(domain)
      request(Domain.Delete(app.id, domain.id))
    }
  }

}

