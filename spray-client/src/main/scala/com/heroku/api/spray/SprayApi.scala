package com.heroku.api.spray

import spray.json._
import spray.http.HttpHeaders.{Authorization, Accept, RawHeader}
import spray.http.MediaTypes._
import spray.http.HttpProtocols._
import spray.can.client.DefaultHttpClient
import spray.client.HttpConduit
import spray.http._
import spray.http.HttpMethods._
import com.heroku.api._
import concurrent.Future
import akka.actor.{Props, ActorSystem}
import com.heroku.api.PartialResponse
import com.heroku.api.ErrorResponse

//TODO Factor out the Body case classes so that they dont inherit NullOptions.

object SprayApi extends DefaultJsonProtocol with NullOptions with ApiJson {
  implicit val errorFormat = jsonFormat2(ErrorResponse)

  implicit val createAppFormat = jsonFormat3(CreateAppBody)

  implicit val updateAppFormat = jsonFormat3(UpdateAppBody)

  implicit val appOwnerFormat = jsonFormat2(AppOwner)

  implicit val appFormat = jsonFormat12(HerokuApp)

  implicit val updateAccount = jsonFormat2(UpdateAccount)

  implicit val account = jsonFormat9(Account)

  implicit val collabBody = jsonFormat1(CollaboratorBody)

  implicit val collabedUser = jsonFormat2(CollaboratedUser)

  implicit val collaborator = jsonFormat3(Collaborator)

  implicit val configVars = mapFormat[String, Option[String]]

  implicit val domainApp = jsonFormat1(DomainApp)

  implicit val domain = jsonFormat5(Domain)

  implicit val createDomain = jsonFormat1(CreateDomainBody)

  implicit val createDyno = jsonFormat2(CreateDynoBody)

  implicit val dynoRelease = jsonFormat1(DynoRelease)

  implicit val dyno = jsonFormat9(Dyno)

  implicit val updateFormationBody = jsonFormat1(UpdateFormationBody)

  implicit val formation = jsonFormat5(Formation)

  implicit val key = jsonFormat5(Key)

  implicit val createKey = jsonFormat1(CreateKeyBody)

  implicit val logSession = jsonFormat2(LogSession)

  /*ApiJson impl*/

  implicit val errorResponseFromJson: FromJson[ErrorResponse] = from[ErrorResponse]

  implicit val createAppBodyToJson: ToJson[CreateAppBody] = to[CreateAppBody]

  implicit val updateAppBodyToJson: ToJson[UpdateAppBody] = to[UpdateAppBody]

  implicit val appFromJson: FromJson[HerokuApp] = from[HerokuApp]

  implicit val appListFromJson: FromJson[List[HerokuApp]] = from[List[HerokuApp]]

  implicit val accountFromJson: FromJson[Account] = from[Account]

  implicit val updateAccountToJson: ToJson[UpdateAccount] = to[UpdateAccount]

  implicit val configFromJson: FromJson[Map[String, String]] = from[Map[String, String]]

  implicit val nullSafeConfigToJson: ToJson[Map[String, Option[String]]] = to[Map[String, Option[String]]]

  implicit val configToJson: ToJson[Map[String, String]] = new ToJson[Map[String, String]] {
    def toJson(t: Map[String, String]): String = {
      nullSafeConfigToJson.toJson(t.map {
        case (k, v) => k -> Option(v)
      })
    }
  }

  implicit def collaboratorBodyToJson: ToJson[CollaboratorBody] = to[CollaboratorBody]

  implicit def collaboratedUserFromJson: FromJson[CollaboratedUser] = from[CollaboratedUser]

  implicit def collaboratorFromJson: FromJson[Collaborator] = from[Collaborator]

  implicit def domainAppFromJson: FromJson[DomainApp] = from[DomainApp]

  implicit def domainFromJson: FromJson[Domain] = from[Domain]

  implicit def createDomainBodyToJson: ToJson[CreateDomainBody] = to[CreateDomainBody]

  implicit def createDynoBodyToJson: ToJson[CreateDynoBody] = to[CreateDynoBody]

  implicit def dynoReleaseFromJson: FromJson[DynoRelease] = from[DynoRelease]

  implicit def dynoFromJson: FromJson[Dyno] = from[Dyno]

  implicit def formationFromJson: FromJson[Formation] = from[Formation]

  implicit def formationListFromJson: FromJson[List[Formation]] = from[List[Formation]]

  implicit def updateFormationBodyToJson: ToJson[UpdateFormationBody] = to[UpdateFormationBody]

  implicit def keyFromJson: FromJson[Key] = from[Key]

  implicit def createKeyBodyToJson: ToJson[CreateKeyBody] = to[CreateKeyBody]

  implicit def logSessionFromJson: FromJson[LogSession] = from[LogSession]

  def from[T](implicit f: JsonFormat[T]) = new FromJson[T] {
    def fromJson(json: String): T = JsonParser(json).convertTo[T]
  }

  def to[T](implicit f: JsonFormat[T]) = new ToJson[T] {
    def toJson(t: T): String = t.toJson.prettyPrint
  }

}

class SprayApi(system: ActorSystem) extends Api {

  import SprayApi._

  val connection = DefaultHttpClient(system)

  val log = system.log

  val conduit = system.actorOf(
    props = Props(new HttpConduit(connection, endpoint, port = 443, sslEnabled = true))
  )

  val pipeline = HttpConduit.sendReceive(conduit)

  val ApiMediaType = MediaTypes.register(CustomMediaType(Request.v3json))

  val accept = Accept(ApiMediaType)

  def creds(key: String) = BasicHttpCredentials("", key)

  def auth(key: String) = Authorization(creds(key))

  def rangeHeader(range: String) = RawHeader("Range", range)

  def endpoint: String = "api.heroku.com"

  implicit val executionContext = system.dispatcher

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T]): Future[Either[ErrorResponse, T]] = {
    val method = getMethod(request)
    val headers = getHeaders(request, key)
    pipeline(HttpRequest(method, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.entity.asString)
    }
  }

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O]): Future[Either[ErrorResponse, O]] = {
    val method = getMethod(request)
    val headers = getHeaders(request, key)
    pipeline(HttpRequest(method, request.endpoint, headers, HttpBody(`application/json`, to.toJson(request.body)), `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.entity.asString)
    }
  }

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]] = {
    val range = request.range.map {
      r => List(rangeHeader(r))
    }.getOrElse(Nil)
    val headers = getHeaders(request, key) ++ range
    pipeline(HttpRequest(GET, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.header[NextRange].map(_.value), resp.entity.asString)
    }
  }

  def getMethod(req: BaseRequest): HttpMethod = {
    req.method match {
      case Request.GET => GET
      case Request.PUT => PUT
      case Request.POST => POST
      case Request.DELETE => DELETE
    }
  }

  def getHeaders(req: BaseRequest, key: String): List[HttpHeader] = {
    req.extraHeaders.map {
      case (k, v) => RawHeader(k, v)
    }.toList ++ List(accept, auth(key))
  }

  case class NextRange(next: String) extends HttpHeader {
    def name: String = "Next-Range"

    def lowercaseName: String = "next-range"

    def value: String = next
  }

}
