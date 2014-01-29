package com.heroku.platform.api

import com.heroku.platform.api.Request._

import Slug._

/** A slug is a snapshot of your application code that is ready to run on the platform. */
object Slug {
  import Slug.models._
  object models {
    case class CreateSlugBody(commit: Option[String] = None, process_types: Map[String, String])
    case class SlugBlob(method: String, url: String)
  }
  /** Info for existing slug. */
  case class Info(app_id_or_name: String, slug_id: String) extends Request[Slug] {
    val expect: Set[Int] = expect200
    val endpoint: String = "/apps/%s/slugs/%s".format(app_id_or_name, slug_id)
    val method: String = GET
  }
  /** Create a new slug. For more information please refer to [Deploying Slugs using the Platform API](https://devcenter.heroku.com/articles/platform-api-deploying-slugs?preview=1). */
  case class Create(app_id_or_name: String, commit: Option[String] = None, process_types: Map[String, String]) extends RequestWithBody[models.CreateSlugBody, Slug] {
    val expect: Set[Int] = expect201
    val endpoint: String = "/apps/%s/slugs".format(app_id_or_name)
    val method: String = POST
    val body: models.CreateSlugBody = models.CreateSlugBody(commit, process_types)
  }
}

/** A slug is a snapshot of your application code that is ready to run on the platform. */
case class Slug(commit: Option[String], process_types: Map[String, String], id: String, created_at: String, updated_at: String, blob: models.SlugBlob)

/** json serializers related to Slug */
trait SlugRequestJson {
  implicit def ToJsonCreateSlugBody: ToJson[models.CreateSlugBody]
  implicit def ToJsonSlugBlob: ToJson[models.SlugBlob]
}

/** json deserializers related to Slug */
trait SlugResponseJson {
  implicit def FromJsonSlugBlob: FromJson[models.SlugBlob]
  implicit def FromJsonSlug: FromJson[Slug]
  implicit def FromJsonListSlug: FromJson[collection.immutable.List[Slug]]
}