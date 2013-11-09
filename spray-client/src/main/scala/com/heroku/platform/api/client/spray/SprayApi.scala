package com.heroku.platform.api.client.spray

import _root_.spray.can.Http
import _root_.spray.can.Http.{ HostConnectorInfo, HostConnectorSetup }
import _root_.spray.http.HttpHeaders._
import _root_.spray.http.MediaTypes._
import _root_.spray.http.HttpProtocols._
import _root_.spray.http._
import _root_.spray.http.HttpMethods._
import _root_.spray.client.pipelining._
import com.heroku.platform.api._
import scala.concurrent.Await
import akka.actor.ActorSystem
import concurrent.duration._
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import com.heroku.platform.api.PartialResponse
import spray.http.HttpEntity.Empty
import com.heroku.platform.api.Api.FutureResponse

class SprayApi(system: ActorSystem)(implicit erj: ErrorResponseJson) extends Api {

  import erj._
  implicit val connTimeout = Timeout(10 seconds)
  implicit val executionContext = system.dispatcher

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

  def execute(request: RequestWithEmptyResponse, key: String, headers: Map[String, String]): FutureResponse[Unit] = {
    val method = getMethod(request)
    val sprayHeaders = getHeaders(headers, key)
    pipeline(HttpRequest(method, request.endpoint, sprayHeaders, Empty, `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        val response = request.getResponse(resp.status.intValue, responseHeaders, resp.entity.asString)
        response
    }
  }

  def execute[T](request: Request[T], key: String, headers: Map[String, String])(implicit f: FromJson[T]): FutureResponse[T] = {
    val method = getMethod(request)
    val sprayHeaders = getHeaders(headers, key)
    pipeline(HttpRequest(method, request.endpoint, sprayHeaders, Empty, `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        val response = request.getResponse(resp.status.intValue, responseHeaders, resp.entity.asString)
        response
    }
  }

  def execute[I, O](request: RequestWithBody[I, O], key: String, headers: Map[String, String])(implicit to: ToJson[I], from: FromJson[O]): FutureResponse[O] = {
    val method = getMethod(request)
    val sprayHeaders = getHeaders(headers, key)
    pipeline(HttpRequest(method, request.endpoint, sprayHeaders, HttpEntity(`application/json`, to.toJson(request.body).getBytes("UTF-8")), `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        request.getResponse(resp.status.intValue, responseHeaders, resp.entity.asString)
    }
  }

  def executeList[T](request: ListRequest[T], key: String, headers: Map[String, String])(implicit f: FromJson[List[T]]): FutureResponse[PartialResponse[T]] = {
    val range = request.range.map {
      r => List(rangeHeader(r))
    }.getOrElse(Nil)
    val sprayHeaders = getHeaders(headers, key) ++ range
    pipeline(HttpRequest(GET, request.endpoint, sprayHeaders, Empty, `HTTP/1.1`)).map {
      resp =>
        val responseHeaders = resp.headers.map(h => h.name -> h.value).toMap
        val response = request.getResponse(resp.status.intValue, responseHeaders, resp.header[NextRange].map(_.value), resp.entity.asString)
        response
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

  def getHeaders(headers: Map[String, String], key: String): List[HttpHeader] = {
    headers.map {
      case (k, v) => RawHeader(k, v)
    }.toList ++ List(accept, auth(key))
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
