package com.heroku.platform.api

abstract class DomainSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: DomainRequestJson with DomainResponseJson = aj

  import implicits._

  "Api for Domains" must {
    "operate on Domains" in {
      val app = getApp
      val domain = create(Domain.Create(app.id, "foo.bar.baz.com"))
      val domainList = listAll(Domain.List(app.id))
      domainList.contains(domain) must be(true)
      val domainInfo = info(Domain.Info(app.id, domain.id))
      domainInfo must equal(domain)
      delete(Domain.Delete(app.id, domain.id))
    }
  }

}

