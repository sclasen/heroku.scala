package com.heroku.platform.api.examples

import com.heroku.platform.api._
import com.twitter.finagle.{ http, Http }
import org.apache.commons.codec.binary.Base64
import org.jboss.netty.handler.codec.http._
import scala.concurrent.ExecutionContext
import org.jboss.netty.buffer.{ ChannelBuffers, ChannelBuffer }
import com.twitter.util.{ Future => TwitterFuture, Duration, Throw, Return }
import scala.concurrent.{ Future, Promise }
import com.twitter.finagle.builder.ClientBuilder
import java.util.concurrent.TimeUnit

class FinagleApi extends Api {

  implicit def executionContext: ExecutionContext = ???

  val client = ClientBuilder()
    .codec(http.Http())
    .hosts("api.heroku.com:443")
    .tls("api.heroku.com").hostConnectionLimit(25).tcpConnectTimeout(Duration(5, TimeUnit.SECONDS))
    .build()

  def withHeaders(request: DefaultHttpRequest, key: String, headers: Map[String, String]): DefaultHttpRequest = {
    val hdrs = Map("Authorization" -> ("Basic " + Base64.encodeBase64String((":" + key).getBytes)), "Accept" -> Request.v3json, "Host" -> "api.heroku.com")
    (headers ++ hdrs).foreach {
      case (k, v) => request.addHeader(k, v)
    }
    request
  }

  def getMethod(req: BaseRequest): HttpMethod = {
    req.method match {
      case Request.GET => HttpMethod.GET
      case Request.PUT => HttpMethod.PUT
      case Request.POST => HttpMethod.POST
      case Request.DELETE => HttpMethod.DELETE
      case Request.PATCH => HttpMethod.PATCH
    }
  }

  def fromTwitter[A](twitterFuture: TwitterFuture[A]): Future[A] = {
    val promise = Promise[A]()
    twitterFuture respond {
      case Return(a) => promise success a
      case Throw(e) => promise failure e
    }
    promise.future
  }

  def execute(request: RequestWithEmptyResponse, key: String, headers: Map[String, String])(implicit e: FromJson[ErrorResponse]): Api.FutureResponse[Unit] = {
    val req = withHeaders(new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), request.endpoint), key, headers)
    fromTwitter {
      client(req).map {
        resp =>
          request.getResponse(resp.getStatus.getCode, Map.empty, resp.getContent.toString("UTF-8"))
      }
    }
  }

  def execute[T](request: Request[T], key: String, headers: Map[String, String])(implicit f: FromJson[T], e: FromJson[ErrorResponse]): Api.FutureResponse[T] = {
    val req = withHeaders(new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), request.endpoint), key, headers)
    fromTwitter {
      client(req).map {
        resp =>
          request.getResponse(resp.getStatus.getCode, Map.empty, resp.getContent.toString("UTF-8"))
      }
    }
  }

  def execute[I, O](request: RequestWithBody[I, O], key: String, headers: Map[String, String])(implicit to: ToJson[I], from: FromJson[O], e: FromJson[ErrorResponse]): Api.FutureResponse[O] = {
    val json = to.toJson(request.body)
    val buf = ChannelBuffers.copiedBuffer(to.toJson(request.body), "UTF-8")
    val req = withHeaders(new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), request.endpoint), key, headers ++ Map("Content-Type" -> "application/json", "Content-Length" -> json.length.toString))
    req.setContent(buf)
    fromTwitter {
      client(req).map {
        resp =>
          request.getResponse(resp.getStatus.getCode, Map.empty, resp.getContent.toString("UTF-8"))
      }
    }
  }

  def executeList[T](request: ListRequest[T], key: String, headers: Map[String, String])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): Api.FutureResponse[PartialResponse[T]] = ???

}
