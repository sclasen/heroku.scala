package com.heroku.api

import concurrent.{ExecutionContext, Future}

trait Api {

  implicit def executionContext:ExecutionContext

  def execute[T](request: Request[T], key: String)(implicit f: FromJson[T]): Future[Either[ErrorResponse, T]]

  def execute[I, O](request: RequestWithBody[I, O], key: String)(implicit to: ToJson[I], from: FromJson[O]): Future[Either[ErrorResponse, O]]

  def executeList[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, PartialResponse[T]]]

  def executeListAll[T](request: ListRequest[T], key: String)(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, List[T]]] = listAll(request,key,List.empty)

  private def listAll[T](request: ListRequest[T], key: String, acc:List[T])(implicit f: FromJson[List[T]]): Future[Either[ErrorResponse, List[T]]] ={
    executeList(request,key).flatMap{
      case Left(e) => Future.successful(Left(e))
      case Right(p) if p.isComplete => Future.successful(Right(acc ++ p.list))
      case Right(p) => listAll(request.nextRequest(p.nextRange.get),key, acc ++ p.list)
    }
  }
}

trait ApiJson extends HerokuAppJson with AccountJson with CollaboratorJson with ConfigVarJson with DomainJson with DynoJson{
  implicit def errorResponseFromJson: FromJson[ErrorResponse]
}
