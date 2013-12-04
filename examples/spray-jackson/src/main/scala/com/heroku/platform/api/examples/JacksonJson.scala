package com.heroku.platform.api.examples

import com.heroku.platform.api._
import com.heroku.platform.api.Key.models.CreateKeyBody
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.`type`.TypeReference

object JacksonJson extends ErrorResponseJson with KeyRequestJson with KeyResponseJson {

  val mapper = new ObjectMapper()

  mapper.registerModule(DefaultScalaModule)

  implicit def ToJsonCreateKeyBody: ToJson[CreateKeyBody] = to[CreateKeyBody]

  implicit def FromJsonKey: FromJson[Key] = from[Key]

  implicit def FromJsonListKey: FromJson[List[Key]] = from[List[Key]]

  implicit def FromJsonErrorResponse: FromJson[ErrorResponse] = from[ErrorResponse]

  def to[T]: ToJson[T] = new ToJson[T] {
    def toJson(t: T): String = {
      mapper.writeValueAsString(t)
    }
  }

  def from[T: Manifest]: FromJson[T] = new FromJson[T] {
    import java.lang.reflect.{ Type, ParameterizedType }

    def fromJson(value: String): T =
      mapper.readValue(value, typeReference[T])

    private[this] def typeReference[T: Manifest] = new TypeReference[T] {
      override def getType = typeFromManifest(manifest[T])
    }

    private[this] def typeFromManifest(m: Manifest[_]): Type = {
      if (m.typeArguments.isEmpty) { m.runtimeClass }
      else new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = null
      }
    }
  }
}
