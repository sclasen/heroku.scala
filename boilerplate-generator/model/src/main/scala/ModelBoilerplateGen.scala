
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

  def e(a: AnyRef) = System.err.println(a)

  //VAL("name", StringClass)

  def codez = {
    val properties = schemaObj.properties
    val links = schemaObj.links
    val typez = properties.keys
    typez.map {
      t =>
        val objDef = properties(t)
        val actionsDef = links(t)
        (BLOCK(
          IMPORT("com.heroku.platform.api._"),
          IMPORT("com.heroku.platform.api.Request._"),
          model(objDef),
          companion(objDef, actionsDef),
          reqJson(objDef, actionsDef),
          respJson(objDef, actionsDef)
        ).inPackage(sym.ApiPackage): Tree)
    }
  }

  def model(modelJson: ModelInfo) = {
    val params = modelJson.mapPropTypeInfo {
      (k, typ) => (PARAM(k, sym.TypeMap(typ.`type`)).tree)
    }
    (CASECLASSDEF(modelJson.name) withParams params: Tree)
  }

  def companion(modelJson: ModelInfo, actionsDefs: List[Action]) = {
    val name: String = modelJson.name
    val actionCaseClasses = actionsDefs.map {
      actionObj =>
        val paramsMap = extractArgumentsFromPath(actionObj) ++
          actionObj.schema.map {
            _.mapPropTypeInfo {
              (k, typ) =>
                System.err.println(k)
                (k -> (PARAM(k, sym.TypeMap(typ.`type`)).tree))
            }
          }.getOrElse {
            Seq.empty[(String, ValDef)]
          }

        val params = paramsMap.toSeq.map(_._2)
        val paramNames = paramsMap.toSeq.map(_._1)
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
    val params = actionObj.schema.map {
      _.mapPropTypeInfo {
        (k, typ) =>
          (PARAM(k, sym.TypeMap(typ.`type`)).tree)
      }
    }.getOrElse {
      Seq.empty[ValDef]
    }

    (CASECLASSDEF(s"${
      actionObj.title
    }${model}Body") withParams params.toIterable)
  }

  def createAction(modelJson: ModelInfo, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    System.err.println(paramNames)
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"models.Create${modelJson.name}Body", modelJson.name)) := BLOCK(
      expect("expect201"), endpoint(actionObj.href), method("POST"),
      (VAL("body", s"models.Create${modelJson.name}Body") := (REF(s"models.Create${modelJson.name}Body") APPLY (paramNames.map(REF(_))))
      )): Tree)
  }

  def listAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.ListRequest TYPE_OF (modelJson.name)) := BLOCK(
      endpoint(actionObj.href), method("GET"),
      (DEF("nextRequest", (sym.ListRequest TYPE_OF (modelJson.name))) withParams ((VAL("nextRange", "String"))) := THIS DOT "copy" APPLY (REF("range") := SOME(REF("nextRange"))))))
  }

  def infoAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.name)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("GET")): Tree)
  }

  def updateAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"Update${modelJson.name}Body", modelJson.name)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("PUT")): Tree)
  }

  def deleteAction(modelJson: ModelInfo, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.name)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("DELETE")): Tree)
  }

  def expect(exRef: String) = (VAL("expect", TYPE_SET(IntClass)) := REF(exRef))
  def endpoint(endRef: String) = (VAL("endpoint", StringClass) := LIT(endRef + "/"))
  def method(methRef: String) = (VAL("method", StringClass) := REF("DELETE"))

  def extractArgumentsFromPath(actionDef: Action) = {
    val rx = """\{([a-zA-Z0-9_]+)\}*""".r
    rx.findAllIn(actionDef.href).map(_.replaceAll("\\{", "").replaceAll("\\}", "")).map(name => name -> (PARAM(name, "String").tree)).toSeq
  }

  def reqJson(modelJson: ModelInfo, actionsDefs: List[Action]) = {
    TRAITDEF(s"${modelJson.name}RequestJson") := BLOCK(LIT("reqJson"))
  }

  def respJson(modelJson: ModelInfo, actionsDefs: List[Action]) = {
    TRAITDEF(s"${modelJson.name}ResponseJson") := BLOCK(LIT("reqJson"))
  }

  val schemaObj = SchemaModel.schemaObj

  case class RefInfo($ref: String)

  case class TypeInfo(`type`: String)

  case class Schema(`type`: String, properties: Map[String, Either[RefInfo, TypeInfo]]) {
    def mapPropTypeInfo[T](funk: (String, TypeInfo) => T) = Typez.mapPropTypeInfo(properties, funk)
  }

  case class Action(title: String, rel: String, href: String, method: String, schema: Option[Schema]) {

  }

  case class ModelInfo(`type`: String, id: String, name: String, description: String, properties: Map[String, Either[RefInfo, TypeInfo]]) {
    def mapPropTypeInfo[T](funk: (String, TypeInfo) => T) = Typez.mapPropTypeInfo(properties, funk)
  }

  object Typez {
    def mapPropTypeInfo[T](properties: Map[String, Either[RefInfo, TypeInfo]], funk: (String, TypeInfo) => T) = {
      val keys = properties.keySet
      keys.map {
        k =>
          val prop = properties(k)
          prop.right.map(
            typ => funk(k, typ)
          ).fold(r => None, x => Some(x))
      }.flatten
    }
  }

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
