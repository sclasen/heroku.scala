
import java.io.File
import scala.io.Source
import spray.json._
import treehugger.forest._
import definitions._
import treehuggerDSL._

object ModelBoilerplateGen extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api.model"
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
        (BLOCK(IMPORT("com.heroku.platform.api._"), IMPORT("com.heroku.platform.api.Request._"), model(objDef), companion(objDef, actionsDef)).inPackage(sym.ApiPackage): Tree)
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
            typ => {
              (PARAM(k, sym.TypeMap(typ.`type`)).tree)
            }
          ).fold(r => None, x => Some(x))
      }.flatten
    }.toIterable.asInstanceOf[Iterable[ValDef]]
    (CASECLASSDEF(modelJson.name) withParams params: Tree)

  }

  def companion(modelJson: ModelInfo, actionsDefs: List[Action]) = {
    val name: String = modelJson.name
    val actionCaseClasses = actionsDefs.map {
      actionObj =>
        def paramsMap = {
          val props = actionObj.schema.map(_.properties).getOrElse(Map.empty)
          val keys = props.keySet
          extractArgumentsFromPath(actionObj) ++
            keys.map {
              k =>
                val prop = props(k)
                prop.right.map(
                  typ => {
                    (k -> (PARAM(k, sym.TypeMap(typ.`type`)).tree))
                  }
                ).fold(r => None, x => Some(x))
            }.flatten
        }.toIterable.asInstanceOf[Iterable[(String, ValDef)]]

        val params = paramsMap.map(_._2)
        val paramNames = paramsMap.map(_._1)
        val extra = extraParams(actionObj)

        actionObj.rel match {
          case "create" => createAction(modelJson, paramNames, params, extra, actionObj)
          case "list" => listAction(modelJson, params, extra, actionObj)
          case "info" => infoAction(modelJson, params, extra, actionObj)
          case "update" => updateAction(modelJson, params, extra, actionObj)
          case "delete" => deleteAction(modelJson, params, extra, actionObj)
        }
    }

    val modelCaseClasses = actionsDefs.map {
      a =>
        a.rel match {
          case "create" => Some(bodyCaseClass(a, modelJson.name))
          case "list" => None
          case "info" => None
          case "update" => Some(bodyCaseClass(a, modelJson.name))
          case "delete" => None
          case _ => None
        }
    }.flatten

    OBJECTDEF(name) := BLOCK(
      actionCaseClasses ++
        Seq((OBJECTDEF("models") := BLOCK(modelCaseClasses)))
    )
  }

  def extraParams(actionObj: Action): Seq[ValDef] = {
    val defs: Seq[ValDef] = if (actionObj.rel == "list") {
      Seq((PARAM("range", TYPE_OPTION("String")) := NONE))
    } else Seq.empty[ValDef]
    defs ++ Seq(PARAM("extraHeaders", TYPE_MAP("String", "String")).tree)
  }

  def bodyCaseClass(actionObj: Action, model: String) = {
    val props: Map[String, Either[RefInfo, TypeInfo]] = actionObj.schema.map(_.properties).getOrElse(Map.empty)
    val keys = props.keySet
    val params = keys.map {
      k =>
        val prop = props(k)
        prop.right.map(
          typ => {
            (PARAM(k, sym.TypeMap(typ.`type`)).tree)
          }
        ).fold(r => None, x => Some(x))
    }.flatten

    (CASECLASSDEF(s"${
      actionObj.title
    }${model}Body") withParams params)
  }

  def createAction(modelJson: ModelInfo, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"models.Create${modelJson.name}Body", modelJson.name)) := BLOCK(
      (VAL("expect", TYPE_SET(IntClass)) := REF("expect201")),
      (VAL("endpoint", StringClass) := LIT(actionObj.href)),
      (VAL("method", StringClass) := REF("POST")),
      (VAL("body", s"models.Create${modelJson.name}Body") := (REF(s"models.Create${modelJson.name}Body") APPLY (paramNames.map(REF(_))))
      )): Tree)
  }

  def listAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.ListRequest TYPE_OF (modelJson.name)) := BLOCK(
      (VAL("endpoint", StringClass) := LIT(actionObj.href + "/")),
      (VAL("method", StringClass) := REF("GET")),
      (DEF("nextRequest", (sym.ListRequest TYPE_OF (modelJson.name))) withParams ((VAL("nextRange", "String"))) := THIS DOT "copy" APPLY (REF("range") := SOME(REF("nextRange"))))))
  }

  def infoAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.name)) := BLOCK(
      (VAL("expect", TYPE_SET(IntClass)) := REF("expect200")),
      (VAL("endpoint", StringClass) := LIT(actionObj.href + "/")),
      (VAL("method", StringClass) := REF("GET"))): Tree)
  }

  def updateAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"Update${modelJson.name}Body", modelJson.name)) := BLOCK(
      (VAL("expect", TYPE_SET(IntClass)) := REF("expect200")),
      (VAL("endpoint", StringClass) := LIT(actionObj.href + "/")),
      (VAL("method", StringClass) := REF("PUT"))): Tree)
  }

  def deleteAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.name)) := BLOCK(
      (VAL("expect", TYPE_SET(IntClass)) := REF("expect200")),
      (VAL("endpoint", StringClass) := LIT(actionObj.href + "/")),
      (VAL("method", StringClass) := REF("DELETE"))): Tree)
  }

  def extractArgumentsFromPath(actionDef: Action) = {
    val rx = """\{([a-zA-Z0-9_]+)\}*""".r
    rx.findAllIn(actionDef.href).map(_.replaceAll("\\{", "").replaceAll("\\}", "")).map(name => name -> (PARAM(name, "String").tree))
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

    //System.err.println(schemaFile.getAbsolutePath)

    def schema = Source.fromFile(schemaFile).foldLeft(new StringBuilder) {
      case (b, c) => b.append(c)
    }.toString

    //System.err.println(schema)

  }

  codez.foreach(c => println(treeToString(c)))

}
