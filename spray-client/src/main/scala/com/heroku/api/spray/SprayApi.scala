package com.heroku.api.spray

import spray.json._
import spray.http.HttpHeaders.{Accept, RawHeader}
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

object SprayApi extends DefaultJsonProtocol with NullOptions {
  implicit val errorFormat = jsonFormat2(ErrorResponse)

  implicit val createAppFormat = jsonFormat3(CreateApp)

  implicit val updateAppFormat = jsonFormat3(UpdateApp)

  implicit val appOwnerFormat = jsonFormat2(AppOwner)

  implicit val appFormat = jsonFormat12(HerokuApp)

  implicit val errFrom: FromJson[ErrorResponse] = from[ErrorResponse]

  implicit val createAppTo: ToJson[CreateApp] = to[CreateApp]

  implicit val updateAppTo: ToJson[UpdateApp] = to[UpdateApp]

  implicit val appFrom:FromJson[HerokuApp] = from[HerokuApp]

  implicit val appListFrom:FromJson[List[HerokuApp]] = from[List[HerokuApp]]

  implicit val upadateAccount = jsonFormat2(UpdateAccount)

  implicit val updateAcctTo:ToJson[UpdateAccount] = to[UpdateAccount]

  implicit val account = jsonFormat9(Account)

  implicit val acctFrom:FromJson[Account] = from[Account]


  def from[T](implicit f:JsonFormat[T]) = new FromJson[T] {
    def fromJson(json: String): T = JsonParser(json).convertTo[T]
  }

  def to[T](implicit f:JsonFormat[T]) = new ToJson[T] {
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

  implicit val ctx = system.dispatcher

  def creds(key: String) = BasicHttpCredentials("", key)

  def endpoint: String = "api.heroku.com"

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T]): Future[Either[ErrorResponse, T]] = {
    val method = getMethod(request)
    val auth = RawHeader("AUTHORIZATION", creds(key).value)
    val headers = getHeaders(request) ++ List(auth)
    pipeline(HttpRequest(method, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        log.info(s"response: ${resp.entity.asString}")
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.entity.asString)
    }
  }

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O]): Future[Either[ErrorResponse, O]] = {
    val method = getMethod(request)
    val auth = RawHeader("AUTHORIZATION", creds(key).value)
    val headers = getHeaders(request) ++ List(auth)
    pipeline(HttpRequest(method, request.endpoint, headers, HttpBody(`application/json`, to.toJson(request.body)), `HTTP/1.1`)).map {
      resp =>
        log.info(s"response: ${resp.entity.asString}")
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.entity.asString)
    }
  }

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]] = {
    val auth = RawHeader("AUTHORIZATION", creds(key).value)
    val range = request.range.map {
      r => List(RawHeader("Range", r))
    }.getOrElse(Nil)
    val headers = getHeaders(request) ++ List(auth) ++ range
    pipeline(HttpRequest(GET, request.endpoint, headers, EmptyEntity, `HTTP/1.1`)).map {
      resp =>
        log.info(s"response: ${resp.entity.asString}")
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.value, responseHeaders, resp.entity.asString)
    }
  }

  def getMethod(req: BaseRequest): HttpMethod = {
    req.method match {
      case "GET" => GET
      case "PUT" => PUT
      case "POST" => POST
      case "DELETE" => DELETE
    }
  }

  def getHeaders(req: BaseRequest): List[HttpHeader] = {
    req.extraHeaders.map {
      case (k, v) => RawHeader(k, v)
    }.toList ++ List(accept)
  }


}
