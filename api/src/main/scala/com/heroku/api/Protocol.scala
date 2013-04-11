package com.heroku.api

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

object ApiCacheError extends ErrorResponse("cache-error", "a 304 was returned but the response was not present in the cache")

trait ApiCache {

  def put[T](request: Request[T], lastModified: String, response: T)

  def put[T](request: ListRequest[T], lastModified: String, response: PartialResponse[T])

  def getLastModified(request: BaseRequest): Option[String]

  def getCachedResponse[T](request: Request[T]): Option[T]

  def getCachedResponse[T](request: ListRequest[T]): Option[PartialResponse[T]]

}

