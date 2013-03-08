package com.heroku.api


trait ToJson[T] {
  def toJson(t: T): String
}

trait FromJson[T] {
  def fromJson(json: String): T
}

