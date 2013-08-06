package com.heroku.api.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.heroku.api._
import com.heroku.api.spray.SprayApi._

class DomainSpec extends WordSpec with SprayApiSpec with MustMatchers {

  "Spray Api for Domains" must {
    "operate on Domains" in {
      val app = getApp
      val domain = create(Domain.Create(app.id, "foo.bar.baz.com"))
      val domainList = listAll(Domain.List(app.id))
      domainList.contains(domain) must be(true)
      val domainInfo =  info(Domain.Info(app.id, domain.id))
      domainInfo must equal(domain)
      delete(Domain.Delete(app.id, domain.id))
    }
  }

}
