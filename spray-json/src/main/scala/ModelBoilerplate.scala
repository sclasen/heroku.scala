import com.heroku.platform.api.{ FromJson, ToJson, ApiRequestJson, ApiResponseJson }
import java.lang.reflect._
import java.lang.reflect.{ Type => JType }

import spray.json._
import treehugger.forest._
import definitions._
import treehuggerDSL._

object ModelBoilerplate extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api._"
    val ClientPackage = "com.heroku.platform.api.client.spray"
    val SprayPackage = "spray.json._"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
    val Request = RootClass.newClass("Request")
    val TypeMap = Map("string" -> "String")

  }

  //VAL("name", StringClass)

  def codez = {
    val properties = schemaObj.fields("properties").asJsObject(errorMsg = "Couldnt get properties obj")
    val links = schemaObj.fields("links").asJsObject(errorMsg = "Couldnt get properties obj")
    val typez = properties.fields.keys
    typez.map {
      t =>
        val objDef = properties.fields(t).asJsObject(errorMsg = s"couldnt get object def for $t")
        val actionsDef = links.fields(t).asInstanceOf[JsArray]
        (BLOCK(model(objDef), companion(objDef, actionsDef)).inPackage("com.heroku.api"): Tree)
    }
  }

  def model(modelJson: JsObject) = {
    val fields = modelJson.fields
    def params = {
      val props: JsObject = fields("properties").asJsObject()
      val keys = props.fields.keySet
      keys.map {
        k =>
          println(k + "!!!!!")
          val prop = props.fields(k).asJsObject
          println(prop.fields.keys)
          prop.fields.get("type").map(
            typ =>
              {
                val value: String = typ.asInstanceOf[JsString].value
                (VAL(k, sym.TypeMap(value)).tree)
              }
          )
      }.flatten
    }.toIterable.asInstanceOf[Iterable[ValDef]]
    (CASECLASSDEF(fields("name").asInstanceOf[JsString].value) withParams params: Tree)

  }

  def companion(modelJson: JsObject, actionsDefs: JsArray) = {
    val fields = modelJson.fields
    val name: String = fields("name").asInstanceOf[JsString].value
    OBJECTDEF(name) := BLOCK(
      actionsDefs.elements.map(_.asJsObject).map {
        actionObj =>
          def params = {
            val props: JsObject = actionObj.fields("schema").asJsObject.fields("properties").asJsObject
            val keys = props.fields.keySet
            keys.map {
              k =>
                println(k + "!!!!!")
                val prop = props.fields(k).asJsObject
                println(prop.fields.keys)
                prop.fields.get("type").map(
                  typ =>
                    {
                      val value: String = typ.asInstanceOf[JsString].value
                      (VAL(k, sym.TypeMap(value)).tree)
                    }
                )
            }.flatten ++ Set(VAL("extraHeaders", TYPE_MAP("String", "String")).tree)
          }.toIterable.asInstanceOf[Iterable[ValDef]]
          (CASECLASSDEF(actionObj.fields("title").asInstanceOf[JsString].value) withParams params withParents (sym.Request TYPE_OF name): Tree)
      }
    )
  }

  val schemaObj = JsonParser.apply(schema).asJsObject(errorMsg = "couldnt parse schema")

  def schema =
    """
      {
        "docVersion": "1",
        "properties": {
          "app":{
            "type":        "object",
            "id":          "app",
            "name":        "App",
            "description": "A Heroku app",
            "properties": {
              "id": {
                "type":        "string",
                "format":      "uuid",
                "description": "Unique app identifier",
                "example":     "01234567-89ab-cdef-0123-456789abcdef"
              },
              "name": {
                "type":        "string",
                "description": "unique name of app",
                "example":     "example"
              },
              "owner": {
                "$ref": "AppOwner"
              }
            }
          }
        },
        "links": {
          "app": [
            {
              "title":  "Create",
              "rel":    "create",
              "href":   "/apps",
              "method": "POST",
              "schema": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string"
                  }
                }
              }
            }
          ]
        }
      }
    """

  codez.foreach(c => println(treeToString(c)))

}