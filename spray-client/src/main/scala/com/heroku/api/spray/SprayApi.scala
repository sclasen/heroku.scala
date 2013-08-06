package com.heroku.api.spray

import _root_.spray.can.Http
import _root_.spray.can.Http.{ HostConnectorInfo, HostConnectorSetup }
import spray.json._
import spray.http.HttpHeaders._
import spray.http.MediaTypes._
import spray.http.HttpProtocols._
import spray.http._
import spray.http.HttpMethods._
import spray.client.pipelining._
import com.heroku.api._
import scala.concurrent.{ Await, Future }
import akka.actor.{ ActorSystem }
import com.heroku.api.PartialResponse
import com.heroku.api.ErrorResponse
import concurrent.duration._
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import com.heroku.api.Account.{ PasswordChangeBody, UpdateBody }
import com.heroku.api.HerokuApp.{ AppOwner, UpdateAppBody, CreateAppBody, AppRegion }
import com.heroku.api.Collaborator.{ CollaboratedUser, CollaboratorBody }
import com.heroku.api.Domain.CreateDomainBody
import com.heroku.api.Dyno.{ CreateDynoBody, DynoRelease }
import com.heroku.api.Formation.UpdateFormationBody
import com.heroku.api.Key.CreateKeyBody
import com.heroku.api.OAuthToken.{ Authorization => OAuthTokenAuthorization }
import com.heroku.api.OAuthAuthorization.{ CreateAuthorizationClient, CreateAuthorizationBody }

object SprayIgnoreNullJson extends DefaultJsonProtocol with ApiRequestJson {

  implicit val appOwnweFormat = jsonFormat2(AppOwner)

  implicit val appRegionFormat = jsonFormat2(AppRegion)

  implicit val updateAccount = jsonFormat3(UpdateBody)

  implicit val passwordChange = jsonFormat2(PasswordChangeBody)

  implicit val createDomain = jsonFormat1(CreateDomainBody)

  implicit val createDyno = jsonFormat2(CreateDynoBody)

  implicit val createAppFormat = jsonFormat3(CreateAppBody)

  implicit val updateAppFormat = jsonFormat3(UpdateAppBody)

  implicit val collabBody = jsonFormat1(CollaboratorBody)

  implicit val updateFormationBody = jsonFormat1(UpdateFormationBody)

  implicit val createKey = jsonFormat1(CreateKeyBody)

  implicit val createAuthClient = jsonFormat1(CreateAuthorizationClient)

  implicit val createAuthBody = jsonFormat3(CreateAuthorizationBody)

  implicit val createAppBodyToJson: ToJson[CreateAppBody] = to[CreateAppBody]

  implicit val updateAppBodyToJson: ToJson[UpdateAppBody] = to[UpdateAppBody]

  implicit val appOwnerToJson: ToJson[AppOwner] = to[AppOwner]

  implicit val updateAccountToJson: ToJson[UpdateBody] = to[UpdateBody]

  implicit val passwordChangeToJson: ToJson[PasswordChangeBody] = to[PasswordChangeBody]

  implicit val nullSafeConfigToJson: ToJson[Map[String, Option[String]]] = to[Map[String, Option[String]]]

  implicit val configToJson: ToJson[Map[String, String]] = new ToJson[Map[String, String]] {
    def toJson(t: Map[String, String]): String = {
      nullSafeConfigToJson.toJson(t.map {
        case (k, v) => k -> Option(v)
      })
    }
  }

  implicit val collaboratorBodyToJson: ToJson[CollaboratorBody] = to[CollaboratorBody]

  implicit val createDomainBodyToJson: ToJson[CreateDomainBody] = to[CreateDomainBody]

  implicit val createDynoBodyToJson: ToJson[CreateDynoBody] = to[CreateDynoBody]

  implicit val updateFormationBodyToJson: ToJson[UpdateFormationBody] = to[UpdateFormationBody]

  implicit val createKeyBodyToJson: ToJson[CreateKeyBody] = to[CreateKeyBody]

  implicit def oauthCreateAuthoriztionClient: ToJson[CreateAuthorizationClient] = to[CreateAuthorizationClient]

  implicit def oauthcreateAuthorizationBody: ToJson[CreateAuthorizationBody] = to[CreateAuthorizationBody]

  def to[T](implicit f: JsonFormat[T]) = new ToJson[T] {
    def toJson(t: T): String = t.toJson.compactPrint
  }

}

object SprayApi extends DefaultJsonProtocol with NullOptions with ApiRequestJson with ApiResponseJson {

  implicit val userFormat = jsonFormat2(User)

  implicit val errorFormat = jsonFormat2(ErrorResponse)

  implicit val appRegionFormat = jsonFormat2(AppRegion)

  implicit val appFormat = jsonFormat15(HerokuApp.apply)

  implicit val account = jsonFormat8(Account.apply)

  implicit val collabedUser = jsonFormat2(CollaboratedUser)

  implicit val collaborator = jsonFormat3(Collaborator.apply)

  implicit val configVars = mapFormat[String, Option[String]]

  implicit val domain = jsonFormat4(Domain.apply)

  implicit val dynoRelease = jsonFormat1(DynoRelease)

  implicit val dyno = jsonFormat9(Dyno.apply)

  implicit val formation = jsonFormat5(Formation.apply)

  implicit val key = jsonFormat5(Key.apply)

  implicit val logSession = jsonFormat2(LogSession.apply)

  implicit val region = jsonFormat5(Region.apply)

  implicit val release = jsonFormat6(Release.apply)

  implicit val oauthAuthorizationAccessToken = jsonFormat3(OAuthAuthorization.AccessToken)

  implicit val oauthAuthorizationClient = jsonFormat3(OAuthAuthorization.Client)

  implicit val oauthAuthorizationGrant = jsonFormat3(OAuthAuthorization.Grant)

  implicit val oauthAuthorizationRefreshToken = jsonFormat3(OAuthAuthorization.RefreshToken)

  implicit val oauthTokenAccessToken = jsonFormat3(OAuthToken.AccessToken)

  implicit val oauthTokenRefreshToken = jsonFormat3(OAuthToken.RefreshToken)

  implicit val oauthAuthorizationSession = jsonFormat1(OAuthAuthorization.Session)

  implicit val oauthAuthorization = jsonFormat9(OAuthAuthorization.apply)

  implicit val oauthClient = jsonFormat6(OAuthClient.apply)

  implicit val oauthTokenAuthorization = jsonFormat1(OAuthTokenAuthorization)

  implicit val oauthToken = jsonFormat7(OAuthToken.apply)

  implicit val createAppBodyToJson: ToJson[CreateAppBody] = SprayIgnoreNullJson.createAppBodyToJson

  implicit val updateAppBodyToJson: ToJson[UpdateAppBody] = SprayIgnoreNullJson.updateAppBodyToJson

  implicit val appOwnerToJson: ToJson[AppOwner] = SprayIgnoreNullJson.appOwnerToJson

  implicit val updateAccountToJson: ToJson[UpdateBody] = SprayIgnoreNullJson.updateAccountToJson

  implicit val passwordChangeToJson: ToJson[PasswordChangeBody] = SprayIgnoreNullJson.passwordChangeToJson

  implicit val collaboratorBodyToJson: ToJson[CollaboratorBody] = SprayIgnoreNullJson.collaboratorBodyToJson

  implicit val createDomainBodyToJson: ToJson[CreateDomainBody] = SprayIgnoreNullJson.createDomainBodyToJson

  implicit val createDynoBodyToJson: ToJson[CreateDynoBody] = SprayIgnoreNullJson.createDynoBodyToJson

  implicit val updateFormationBodyToJson: ToJson[UpdateFormationBody] = SprayIgnoreNullJson.updateFormationBodyToJson

  implicit val createKeyBodyToJson: ToJson[CreateKeyBody] = SprayIgnoreNullJson.createKeyBodyToJson

  implicit val configToJson: ToJson[Map[String, String]] = SprayIgnoreNullJson.configToJson

  implicit val oauthCreateAuthoriztionClient: ToJson[CreateAuthorizationClient] = SprayIgnoreNullJson.oauthCreateAuthoriztionClient

  implicit val oauthcreateAuthorizationBody: ToJson[CreateAuthorizationBody] = SprayIgnoreNullJson.oauthcreateAuthorizationBody

  implicit val collaboratedUserFromJson: FromJson[CollaboratedUser] = from[CollaboratedUser]

  implicit val collaboratorFromJson: FromJson[Collaborator] = from[Collaborator]

  implicit val collaboratorListFromJson: FromJson[List[Collaborator]] = from[List[Collaborator]]

  implicit val domainFromJson: FromJson[Domain] = from[Domain]

  implicit val domainListFromJson: FromJson[List[Domain]] = from[List[Domain]]

  implicit val dynoReleaseFromJson: FromJson[DynoRelease] = from[DynoRelease]

  implicit val dynoFromJson: FromJson[Dyno] = from[Dyno]

  implicit val formationFromJson: FromJson[Formation] = from[Formation]

  implicit val formationListFromJson: FromJson[List[Formation]] = from[List[Formation]]

  implicit val errorResponseFromJson: FromJson[ErrorResponse] = from[ErrorResponse]

  implicit val userFromJson: FromJson[User] = from[User]

  implicit val appFromJson: FromJson[HerokuApp] = from[HerokuApp]

  implicit val appListFromJson: FromJson[List[HerokuApp]] = from[List[HerokuApp]]

  implicit val accountFromJson: FromJson[Account] = from[Account]

  implicit val configFromJson: FromJson[Map[String, String]] = from[Map[String, String]]

  implicit val keyFromJson: FromJson[Key] = from[Key]

  implicit val regionFromJson: FromJson[Region] = from[Region]

  implicit val regionListFromJson: FromJson[List[Region]] = from[List[Region]]

  implicit val releaseFromJson: FromJson[Release] = from[Release]

  implicit val releaseListFromJson: FromJson[List[Release]] = from[List[Release]]

  implicit val logSessionFromJson: FromJson[LogSession] = from[LogSession]

  implicit val oauthAuthorizationFromJson: FromJson[OAuthAuthorization] = from[OAuthAuthorization]

  implicit val oauthAuthorizationListFromJson: FromJson[List[OAuthAuthorization]] = from[List[OAuthAuthorization]]

  implicit val oauthClientFromJson: FromJson[OAuthClient] = from[OAuthClient]

  implicit val oauthTokenFromJson: FromJson[OAuthToken] = from[OAuthToken]

  implicit val oauthClientListFromJson: FromJson[List[OAuthClient]] = from[List[OAuthClient]]

  implicit val oauthTokenListFromJson: FromJson[List[OAuthToken]] = from[List[OAuthToken]]

  def from[T](implicit f: JsonFormat[T]) = new FromJson[T] {
    def fromJson(json: String): T = try {
      JsonParser(json).convertTo[T]
    } catch {
      case d: DeserializationException =>
        println(json)
        throw d
    }
  }

}

class SprayApi(system: ActorSystem, apiCache: ApiCache = NoCache) extends Api {

  import SprayApi._

  implicit val connTimeout = Timeout(10 seconds)
  implicit val executionContext = system.dispatcher
  implicit val cache = apiCache

  val connection = {
    implicit val s = system
    Await.result((IO(Http) ? HostConnectorSetup(endpoint, port = 443, sslEncryption = true)).map {
      case HostConnectorInfo(hostConnector, _) => hostConnector
    }, connTimeout.duration)
  }

  val log = system.log

  val pipeline = sendReceive(connection)

  val ApiMediaType = MediaTypes.register(MediaType.custom(Request.v3json))

  val accept = Accept(ApiMediaType)

  def creds(key: String) = BasicHttpCredentials("", key)

  def auth(key: String) = Authorization(creds(key))

  def rangeHeader(range: String) = RawHeader("Range", range)

  def endpoint: String = "api.heroku.com"

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T]): Future[Either[ErrorResponse, T]] = {
    val method = getMethod(request)
    val headers = getHeaders(request, key) ++ ifModified(request, cache)
    pipeline(HttpRequest(method, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        if (resp.status.value == 304) {
          cache.getCachedResponse(request).map(Right(_)).getOrElse(Left(ApiCacheError))
        } else {
          val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
          val response = request.getResponse(resp.status.intValue, responseHeaders, resp.entity.asString)
          response.right.foreach(right => resp.header[`Last-Modified`].foreach(last => cache.put(request, last.value, right)))
          response
        }
    }
  }

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O]): Future[Either[ErrorResponse, O]] = {
    val method = getMethod(request)
    val headers = getHeaders(request, key)
    pipeline(HttpRequest(method, request.endpoint, headers, HttpEntity(`application/json`, to.toJson(request.body).getBytes("UTF-8")), `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.intValue, responseHeaders, resp.entity.asString)
    }
  }

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]] = {
    val range = request.range.map {
      r => List(rangeHeader(r))
    }.getOrElse(Nil)
    val headers = getHeaders(request, key) ++ range ++ ifModified(request, cache)
    pipeline(HttpRequest(GET, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        if (resp.status.value == 304) {
          cache.getCachedResponse(request).map(Right(_)).getOrElse(Left(ApiCacheError))
        } else {
          val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
          val response = request.getResponse(resp.status.intValue, responseHeaders, resp.header[NextRange].map(_.value), resp.entity.asString)
          response.right.foreach(right => resp.header[`Last-Modified`].foreach(last => cache.put(request, last.value, right)))
          response
        }
    }
  }

  def getMethod(req: BaseRequest): HttpMethod = {
    req.method match {
      case Request.GET => GET
      case Request.PUT => PUT
      case Request.POST => POST
      case Request.DELETE => DELETE
      case Request.PATCH => PATCH
    }
  }

  def getHeaders(req: BaseRequest, key: String): List[HttpHeader] = {
    req.extraHeaders.map {
      case (k, v) => RawHeader(k, v)
    }.toList ++ List(accept, auth(key))
  }

  def ifModified(req: Request[_], cache: ApiCache): List[HttpHeader] = {
    cache.getLastModified(req).map(last => List(IfModifiedSince(last))).getOrElse(Nil)
  }

  def ifModified(req: ListRequest[_], cache: ApiCache): List[HttpHeader] = {
    cache.getLastModified(req).map(last => List(IfModifiedSince(last))).getOrElse(Nil)
  }

  case class NextRange(next: String) extends HttpHeader {
    def name: String = "Next-Range"

    def lowercaseName: String = "next-range"

    def value: String = next

    def render[R <: Rendering](r: R): r.type = r
  }

  case class IfModifiedSince(since: String) extends HttpHeader {
    def name: String = "If-Modified-Since"

    def lowercaseName: String = "if-modified-since"

    def value: String = since

    def render[R <: Rendering](r: R): r.type = r
  }

}
