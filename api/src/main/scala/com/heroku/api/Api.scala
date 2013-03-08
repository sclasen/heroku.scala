package com.heroku.api

import concurrent.Future


trait Api {
  def execute[T](request: Request[T], key: String)(implicit f:FromJson[T]): Future[Either[ErrorResponse, T]]

  def execute[I,O](request: RequestWithBody[I,O], key: String)(implicit to:ToJson[I],from:FromJson[O]): Future[Either[ErrorResponse, O]]

  def executeList[T](request: ListRequest[T], key: String)(implicit f:FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]]

}
