package com.heroku.platform.api

import com.heroku.platform.api.Api.FutureResponse
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.language.postfixOps

trait ToJson[T] {
  def toJson(t: T): String
}

trait FromJson[T] {
  def fromJson(json: String): T
}

case class Response[T](status: Int, headers: Map[String, String], body: T)

case class ErrorResponse(id: String, message: String)

case class PartialResponse[T](list: List[T], nextRange: Option[String]) {
  def isComplete = nextRange.isEmpty
}

object Request {
  val v3json = "application/vnd.heroku+json; version=3"
  val expect200 = Set(200)
  val expect201 = Set(201)
  val expect202 = Set(202)
  val GET = "GET"
  val PUT = "PUT"
  val PATCH = "PATCH"
  val POST = "POST"
  val DELETE = "DELETE"
}

trait BaseRequest {

  def expect: Set[Int]

  def endpoint: String

  def method: String

}

trait RequestWithEmptyResponse extends BaseRequest {

  def getResponse(status: Int, headers: Map[String, String], body: String)(implicit e: FromJson[ErrorResponse]): Either[Response[ErrorResponse], Response[Unit]] = {
    if (expect.contains(status)) {
      Right(Response(status, headers, ()))
    } else {
      Left(Response(status, headers, e.fromJson(body)))
    }
  }
}

trait Request[O] extends BaseRequest {

  def getResponse(status: Int, headers: Map[String, String], body: String)(implicit f: FromJson[O], e: FromJson[ErrorResponse]): Either[Response[ErrorResponse], Response[O]] = {
    if (expect.contains(status)) {
      Right(Response(status, headers, f.fromJson(body)))
    } else {
      Left(Response(status, headers, e.fromJson(body)))
    }
  }
}

trait RequestWithBody[I, O] extends Request[O] {

  def body: I

}

trait ListRequest[T] extends BaseRequest {

  val expect = Set(200, 206)

  def range: Option[String]

  def getResponse(status: Int, headers: Map[String, String], nextRange: Option[String], body: String)(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): Either[Response[ErrorResponse], Response[PartialResponse[T]]] = {
    if (status == 200) {
      Right(Response(status, headers, PartialResponse(f.fromJson(body), None)))
    } else if (status == 206) {
      Right(Response(status, headers, PartialResponse(f.fromJson(body), nextRange)))
    } else {
      Left(Response(status, headers, e.fromJson(body)))
    }
  }

  def nextRequest(nextRange: String): ListRequest[T]
}

trait ErrorResponseJson {
  implicit def FromJsonErrorResponse: FromJson[ErrorResponse]
}

object Api {
  type FutureResponse[T] = Future[Either[Response[ErrorResponse], Response[T]]]
}

trait Api {

  implicit def executionContext: ExecutionContext

  def execute(request: RequestWithEmptyResponse, key: String)(implicit e: FromJson[ErrorResponse]): FutureResponse[Unit] = execute(request, key, Map.empty[String, String])

  def execute(request: RequestWithEmptyResponse, key: String, headers: Map[String, String])(implicit e: FromJson[ErrorResponse]): FutureResponse[Unit]

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T], e: FromJson[ErrorResponse]): FutureResponse[T] = execute[T](request, key, Map.empty[String, String])

  def execute[T](request: Request[T], key: String, headers: Map[String, String])(implicit f: FromJson[T], e: FromJson[ErrorResponse]): FutureResponse[T]

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O], e: FromJson[ErrorResponse]): FutureResponse[O] = execute[I, O](request, key, Map.empty[String, String])

  def execute[I, O](request: RequestWithBody[I, O], key: String, headers: Map[String, String])(implicit to: ToJson[I], from: FromJson[O], e: FromJson[ErrorResponse]): FutureResponse[O]

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): FutureResponse[PartialResponse[T]] = executeList[T](request, key, Map.empty[String, String])

  def executeList[T](request: ListRequest[T], key: String, headers: Map[String, String])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): FutureResponse[PartialResponse[T]]

  def executeListAll[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): FutureResponse[List[T]] = executeListAll[T](request, key, Map.empty[String, String])

  def executeListAll[T](request: ListRequest[T], key: String, headers: Map[String, String])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): FutureResponse[List[T]] = listAll(request, key, headers, List.empty)

  private def listAll[T](request: ListRequest[T], key: String, headers: Map[String, String], acc: List[T])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): FutureResponse[List[T]] = {
    executeList(request, key).flatMap {
      case Left(e) => Future.successful(Left(e))
      case Right(p) if p.body.isComplete => Future.successful(Right(Response(p.status, p.headers, acc ++ p.body.list)))
      case Right(p) => listAll(request.nextRequest(p.body.nextRange.get), key, headers, acc ++ p.body.list)
    }
  }
}

case class SimpleApi(api: Api, apiKey: String) {

  implicit val ctx = api.executionContext

  def fold[T](either: Either[Response[ErrorResponse], Response[T]]): T = {
    either.fold(e => sys.error(e.toString), s => s.body)
  }

  def execute(request: RequestWithEmptyResponse)(implicit e: FromJson[ErrorResponse]): Future[Unit] = api.execute(request, apiKey).map(fold)

  def execute[T](request: Request[T])(implicit f: FromJson[T], e: FromJson[ErrorResponse]): Future[T] = api.execute(request, apiKey).map(fold)

  def execute[I, O](request: RequestWithBody[I, O])(implicit to: ToJson[I], from: FromJson[O], e: FromJson[ErrorResponse]): Future[O] = api.execute[I, O](request, apiKey).map(fold)

  def executeListAll[T](request: ListRequest[T])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): Future[List[T]] = api.executeListAll[T](request, apiKey).map(fold)

}

object SyncApi {
  def apply(api: Api, apiKey: String): SyncApi = SyncApi(SimpleApi(api, apiKey))
}

case class SyncApi(api: SimpleApi, timeoutSeconds: Int = 10) {
  implicit val ctx = api.ctx

  import scala.concurrent.duration._

  def waitFor[T](f: Future[T]): T = Await.result(f, timeoutSeconds seconds)

  def execute(request: RequestWithEmptyResponse)(implicit e: FromJson[ErrorResponse]): Unit = waitFor(api.execute(request))

  def execute[T](request: Request[T])(implicit f: FromJson[T], e: FromJson[ErrorResponse]): T = waitFor(api.execute(request))

  def execute[I, O](request: RequestWithBody[I, O])(implicit to: ToJson[I], from: FromJson[O], e: FromJson[ErrorResponse]): O = waitFor(api.execute(request))

  def executeListAll[T](request: ListRequest[T])(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): List[T] = waitFor(api.executeListAll(request))

}