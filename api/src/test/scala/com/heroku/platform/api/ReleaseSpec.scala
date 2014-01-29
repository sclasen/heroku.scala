package com.heroku.platform.api

abstract class ReleaseSpec(aj: ApiRequestJson with ApiResponseJson) extends ApiSpec(aj) {

  val implicits: ReleaseRequestJson with ReleaseResponseJson with ConfigVarRequestJson with ConfigVarResponseJson with SlugRequestJson with SlugResponseJson with ErrorResponseJson = aj

  import implicits._

  "Api for Releases" must {
    "operate on Releases" in {
      import primary._
      val app = getApp

      val updatedConfig = request(ConfigVar.Update(app.id, Map("FOO" -> "BAR")))
      val releases = requestAll(Release.List(app.id))
      releases.size must be >= 2 //logplex can create a release so this should be 2 or 3

      val release = request(Release.Info(app.id, releases(0).id))
      release must equal(releases(0))
      val rolledback = request(Release.Rollback(app.id, release.id))
      rolledback.version must be > release.version

      val slug = request(Slug.Create(app.id, None, Map("clock" -> "bin/clock", "echo" -> "bin/echo")))
      val putUrl = slug.blob.url
      val curlProc = scala.sys.process.Process(s"""curl -X PUT -H Content-Type: --data-binary @api/src/test/resources/test-slug/test-slug.tgz $putUrl""")
      (curlProc !) must equal(0)

      val releasedSlug = request(Release.Create(app.id, Some("test slug release"), slug.id))
      val updatedReleases = requestAll(Release.List(app.id))
      updatedReleases.last must equal(releasedSlug)

    }

  }

}

