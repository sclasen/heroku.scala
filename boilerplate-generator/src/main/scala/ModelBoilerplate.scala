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
    val properties = schemaObj.properties
    val links = schemaObj.links
    val typez = properties.keys
    typez.map {
      t =>
        val objDef = properties(t)
        val actionsDef = links(t)
        (BLOCK(model(objDef), companion(objDef, actionsDef)).inPackage("com.heroku.api"): Tree)
    }
  }

  def model(modelJson: ModelInfo) = {
    def params = {
      val props = modelJson.properties
      val keys = props.keySet
      keys.map {
        k =>
          val prop = props(k)
          prop.right.map(
            typ =>
              {
                (VAL(k, sym.TypeMap(typ.`type`)).tree)
              }
          ).fold(r => None, x => Some(x))
      }.flatten
    }.toIterable.asInstanceOf[Iterable[ValDef]]
    (CASECLASSDEF(modelJson.name) withParams params: Tree)

  }

  def companion(modelJson: ModelInfo, actionsDefs: List[Action]) = {
    val name: String = modelJson.name
    OBJECTDEF(name) := BLOCK(
      actionsDefs.map {
        actionObj =>
          def params = {
            val props = actionObj.schema.properties
            val keys = props.keySet
            keys.map {
              k =>
                val prop = props(k)
                prop.right.map(
                  typ =>
                    {
                      (VAL(k, sym.TypeMap(typ.`type`)).tree)
                    }
                ).fold(r => None, x => Some(x))
            }.flatten ++ Set(VAL("extraHeaders", TYPE_MAP("String", "String")).tree)
          }.toIterable.asInstanceOf[Iterable[ValDef]]
          (CASECLASSDEF(actionObj.title) withParams params withParents (sym.Request TYPE_OF name): Tree)
      }
    )
  }

  val schemaObj = SchemaModel.schemaObj

  case class RefInfo($ref: String)
  case class TypeInfo(`type`: String)
  case class Schema(`type`: String, properties: Map[String, Either[RefInfo, TypeInfo]])
  case class Action(title: String, rel: String, href: String, method: String, schema: Schema)
  case class ModelInfo(`type`: String, id: String, name: String, description: String, properties: Map[String, Either[RefInfo, TypeInfo]])
  case class SchemaDoc(docVersion: String, properties: Map[String, ModelInfo], links: Map[String, List[Action]])

  object SchemaModel extends DefaultJsonProtocol {

    implicit lazy val fti = jsonFormat1(TypeInfo)
    implicit lazy val fri = jsonFormat(RefInfo, "$ref")
    implicit lazy val fs = jsonFormat2(Schema)
    implicit lazy val fa = jsonFormat5(Action)
    implicit lazy val fmi = jsonFormat5(ModelInfo)
    implicit lazy val ti = jsonFormat3(SchemaDoc)

    def schemaObj = JsonParser(schema).convertTo[SchemaDoc]

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
  }

  codez.foreach(c => println(treeToString(c)))

}