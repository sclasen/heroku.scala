package com.heroku.platform.api

import concurrent.{ ExecutionContext, Future }

trait ToJson[T] {
  def toJson(t: T): String
}

trait FromJson[T] {
  def fromJson(json: String): T
}

case class ErrorResponse(id: String, message: String)

case class PartialResponse[T](list: List[T], nextRange: Option[String]) {
  def isComplete = nextRange.isEmpty
}

object Request {
  val v3json = "application/vnd.heroku+json; version=3"
  val expect200 = Set(200)
  val expect201 = Set(201)
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

  def extraHeaders: Map[String, String]

}

trait Request[O] extends BaseRequest {

  def getResponse(status: Int, headers: Map[String, String], body: String)(implicit f: FromJson[O], e: FromJson[ErrorResponse]): Either[ErrorResponse, O] = {
    if (expect.contains(status)) {
      Right(f.fromJson(body))
    } else {
      Left(e.fromJson(body))
    }
  }
}

trait RequestWithBody[I, O] extends Request[O] {

  def body: I

}

trait ListRequest[T] extends BaseRequest {

  val expect = Set(200, 206)

  def range: Option[String]

  def getResponse(status: Int, headers: Map[String, String], nextRange: Option[String], body: String)(implicit f: FromJson[List[T]], e: FromJson[ErrorResponse]): Either[ErrorResponse, PartialResponse[T]] = {
    if (status == 200) {
      Right(PartialResponse(f.fromJson(body), None))
    } else if (status == 206) {
      Right(PartialResponse(f.fromJson(body), nextRange))
    } else {
      Left(e.fromJson(body))
    }
  }

  def nextRequest(nextRange: String): ListRequest[T]
}

trait ErrorResponseJson {
  implicit def FromJsonErrorResponse: FromJson[ErrorResponse]
}

trait Api {

  implicit def executionContext: ExecutionContext

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T]): Future[Either[ErrorResponse, T]]

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O]): Future[Either[ErrorResponse, O]]

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]]

  def executeListAll[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, List[T]]] = listAll(request, key, List.empty)

  private def listAll[T](request: ListRequest[T], key: String, acc: List[T])(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, List[T]]] = {
    executeList(request, key).flatMap {
      case Left(e) => Future.successful(Left(e))
      case Right(p) if p.isComplete => Future.successful(Right(acc ++ p.list))
      case Right(p) => listAll(request.nextRequest(p.nextRange.get), key, acc ++ p.list)
    }
  }
}

