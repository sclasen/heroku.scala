package com.heroku.platform.api.client.spray

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import scala.io.Source
import com.heroku.platform.api._
import _root_.spray.json._
import com.heroku.platform.api.client.spray.SprayApi._
import com.heroku.platform.api.Account
import com.heroku.platform.api.HerokuApp
import com.heroku.platform.api.Collaborator

class JsonSpec extends WordSpec with MustMatchers {

  val apiJson = {
    val apidotjson: String = Source.fromFile("spray-client/src/test/resources/api.json").mkString
    val json: JsValue = JsonParser.apply(apidotjson)
    json.asJsObject("no api")
  }

  def modelFields(model: String) = apiJson.fields("resources")
    .asJsObject("no resource").fields(model)
    .asJsObject(s"no $model").fields("attributes")
    .asJsObject("no atts").fields

  def example(mf: Map[String, JsValue]): JsObject = {
    val (nested, unnested) = mf.keys.map {
      k =>
        val obj = mf(k).asJsObject
        if (obj.fields("serialized").asInstanceOf[JsBoolean].value) {
          val exValue = obj.fields("example")
          Some(k -> exValue)
        } else None
    }.flatten.partition(_._1.contains(":"))

    JsObject((unnest(nested) ++ unnested).toMap)
  }

  def unnest(nested: Iterable[(String, JsValue)]): Iterable[(String, JsValue)] = {

    val by: Map[String, Iterable[(String, JsValue)]] = nested.groupBy {
      case (k, v) => k.substring(0, k.indexOf(":"))
    }

    by.map {
      case (name, nested) => unnest(name, nested)
    }.toIterable

  }

  def unnest(field: String, nested: Iterable[(String, JsValue)]): (String, JsValue) = {
    field -> JsObject(nested.map {
      case (subfield, js) => subfield.substring(field.length + 1) -> js
    }.toMap)
  }

  val modelMap = List(
    "Account" -> implicitly[FromJson[Account]],
    "Add-on" -> implicitly[FromJson[Addon]],
    "App" -> implicitly[FromJson[HerokuApp]],
    "App Transfer" -> implicitly[FromJson[AppTransfer]],
    "Collaborator" -> implicitly[FromJson[Collaborator]],
    "Domain" -> implicitly[FromJson[Domain]],
    "Dyno" -> implicitly[FromJson[Dyno]],
    "Formation" -> implicitly[FromJson[Formation]],
    "Key" -> implicitly[FromJson[Key]],
    "Log Session" -> implicitly[FromJson[LogSession]],
    "Region" -> implicitly[FromJson[Region]],
    "Release" -> implicitly[FromJson[Release]],
    "OAuth Authorization" -> implicitly[FromJson[OAuthAuthorization]],
    "OAuth Client" -> implicitly[FromJson[OAuthClient]],
    "OAuth Token" -> implicitly[FromJson[OAuthToken]]
  )

  "Json" must {
    "Be parsed" in {
      modelMap.foreach {
        case (model, from) =>
          val theExample = example(modelFields(model))
          println(theExample.prettyPrint)
          try {
            val parsed = from.fromJson(theExample.prettyPrint)
            println(parsed)
          } catch {
            case e: Exception => fail(s"$model failed, ${e.getMessage}", e)
          }
      }
    }
  }

}
