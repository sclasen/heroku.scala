
import java.io.File
import scala.io.Source
import spray.json._
import treehugger.forest._
import definitions._
import treehuggerDSL._

object ModelBoilerplateGen extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
    val Request = RootClass.newClass("Request")
    val RequestWithBody = RootClass.newClass("RequestWithBody")
    val ListRequest = RootClass.newClass("ListRequest")
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
        (BLOCK(model(objDef), companion(objDef, actionsDef)).inPackage(sym.ApiPackage): Tree)
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
            val props = actionObj.schema.map(_.properties).getOrElse(Map.empty)
            val keys = props.keySet
            extractArgumentsFromPath(actionObj) ++
              keys.map {
                k =>
                  val prop = props(k)
                  prop.right.map(
                    typ =>
                      {
                        (VAL(k, sym.TypeMap(typ.`type`)).tree)
                      }
                  ).fold(r => None, x => Some(x))
              }.flatten ++ Seq(VAL("extraHeaders", TYPE_MAP("String", "String")).tree)
          }.toIterable.asInstanceOf[Iterable[ValDef]]
          (CASECLASSDEF(actionObj.title) withParams params withParents (sym.Request TYPE_OF name): Tree)
      }
    )
  }

  def extractArgumentsFromPath(actionDef: Action) = {
    val rx = """\{([a-zA-Z0-9_]+)\}*""".r
    rx.findAllIn(actionDef.href).map(_.replaceAll("\\{", "").replaceAll("\\}", "")).map(name => (VAL(name, "String").tree))
  }

  val schemaObj = SchemaModel.schemaObj

  case class RefInfo($ref: String)
  case class TypeInfo(`type`: String)
  case class Schema(`type`: String, properties: Map[String, Either[RefInfo, TypeInfo]])
  case class Action(title: String, rel: String, href: String, method: String, schema: Option[Schema])
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

    def schemaFile = new File("api/src/main/resources/schema.json")

    System.err.println(schemaFile.getAbsolutePath)

    def schema = Source.fromFile(schemaFile).foldLeft(new StringBuilder) {
      case (b, c) => b.append(c)
    }.toString

    System.err.println(schema)

  }

  codez.foreach(c => println(treeToString(c)))

}
