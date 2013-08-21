
import java.net.URLDecoder
import play.api.libs.json._
import java.io.File
import scala.io.Source
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
  }

  import SchemaModel._

  def initialCap(s: String) = {
    val (f, l) = s.splitAt(1)
    s"${
      f.toUpperCase
    }$l"
  }

  def e(a: AnyRef) = System.err.println(a)

  def fieldType(typ: List[String]) = {
    val isOptional = typ.contains("null")
    val typez = typ.filter(_ != "null")
    if (typez.length == 1) {
      if (isOptional) (TYPE_OPTION(initialCap(typez(0))))
      else (TYPE_REF(initialCap(typez(0))))
    } else {
      throw new IllegalStateException("encountered type with more than one non null type value")
    }
  }

  def argType(typ: List[String]) = {
    val typez = typ.filter(_ != "null")
    if (typez.length == 1) {
      (TYPE_OPTION(initialCap(typez(0))))
    } else {
      throw new IllegalStateException("encountered type with more than one non null type value")
    }
  }

  def codez = {
    val properties = schemaObj.as[SchemaDoc].properties
    val typez = properties.keys
    typez.map {
      t =>
        val objDef = properties(t)
        val actionsDef = objDef.links.getOrElse(List.empty)
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

  def model(modelJson: ResourceDef) = {
    val params = modelJson.properties.map {
      case (k, Right(typ)) => (PARAM(k, fieldType(typ.`type`)).tree)
      case (k, Left(nest)) => (PARAM(k, TYPE_REF("models." + modelJson.title + initialCap(k))).tree)
    }
    (CASECLASSDEF(modelJson.title) withParams params: Tree)
  }

  def flattenAction(actions: List[Action]): Action = {
    actions(0)
  }

  def flattenActions(actions: List[Action]): List[Action] = {
    actions.groupBy(_.title).map {
      case (title, actions) if actions.size > 1 => flattenAction(actions)
      case (title, actions) => actions(0)
    }.toList
  }

  def companion(modelJson: ResourceDef, actionsDefs: List[Action]) = {
    val name: String = modelJson.title

    val actionCaseClasses = flattenActions(actionsDefs).map {
      actionObj =>
        val paramsMap = actionObj.mapPropTypeInfo {
          (k, typ) =>
            (k -> (PARAM(k, argType(typ.`type`)) := NONE))
        }

        val params = (extractArgumentsFromPath(actionObj) ++ paramsMap).toSeq.map(_._2)
        val paramNames = paramsMap.toSeq.map(_._1)
        val extra = extraParams(actionObj)

        actionObj.title match {
          case "Create" => createAction(modelJson, paramNames, params, extra, actionObj)
          case "List" => listAction(modelJson, params, extra, actionObj)
          case "Info" => infoAction(modelJson, params, extra, actionObj)
          case "Update" => updateAction(modelJson, paramNames, params, extra, actionObj)
          case "Delete" => deleteAction(modelJson, params, extra, actionObj)
          case x => LIT(x)
        }
    }

    val modelCaseClasses = flattenActions(actionsDefs).map {
      a =>
        a.title match {
          case "Create" => Some(bodyCaseClass(a, modelJson.title))
          case "List" => None
          case "Info" => None
          case "Update" => Some(bodyCaseClass(a, modelJson.title))
          case "Delete" => None
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
    defs ++ Seq(PARAM("extraHeaders", TYPE_MAP("String", "String")) := NONE)
  }

  def bodyCaseClass(actionObj: Action, model: String) = {
    val params = actionObj.mapPropTypeInfo {
      (k, typ) =>
        (PARAM(k, argType(typ.`type`)).tree)
    }

    (CASECLASSDEF(s"${
      actionObj.title
    }${model}Body") withParams params.toIterable)
  }

  def toJson(model: String, typ: String) = {
    DEF(model + "ToJson", sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT)
  }

  def fromJson(model: String, typ: String) = {
    DEF(model + "FromJson", sym.FromJson TYPE_OF typ) withFlags (Flags.IMPLICIT)
  }

  def createAction(modelJson: ResourceDef, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    System.err.println(paramNames)
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"models.Create${modelJson.title}Body", modelJson.title)) := BLOCK(
      expect("expect201"), endpoint(actionObj.href), method("POST"),
      (VAL("body", s"models.Create${modelJson.title}Body") := (REF(s"models.Create${modelJson.title}Body") APPLY (paramNames.map(REF(_))))
      )): Tree)
  }

  def listAction(modelJson: ResourceDef, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.ListRequest TYPE_OF (modelJson.title)) := BLOCK(
      endpoint(actionObj.href), method("GET"),
      (DEF("nextRequest", (sym.ListRequest TYPE_OF (modelJson.title))) withParams ((VAL("nextRange", "String"))) := THIS DOT "copy" APPLY (REF("range") := SOME(REF("nextRange"))))))
  }

  def infoAction(modelJson: ResourceDef, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.title)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("GET")): Tree)
  }

  def updateAction(modelJson: ResourceDef, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"models.Update${modelJson.title}Body", modelJson.title)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("PUT"),
      (VAL("body", s"models.Update${modelJson.title}Body") := (REF(s"models.Update${modelJson.title}Body") APPLY (paramNames.map(REF(_)))))
    ): Tree)
  }

  def deleteAction(modelJson: ResourceDef, params: Iterable[ValDef], extra: Iterable[ValDef], actionObj: Action) = {
    (CASECLASSDEF(actionObj.title) withParams params ++ extra withParents (sym.Request TYPE_OF (modelJson.title)) := BLOCK(
      expect("expect200"), endpoint(actionObj.href), method("DELETE")): Tree)
  }

  def expect(exRef: String) = (VAL("expect", TYPE_SET(IntClass)) := REF(exRef))

  def endpoint(endRef: String) = (VAL("endpoint", StringClass) := LIT(endRef))

  def method(methRef: String) = (VAL("method", StringClass) := REF(methRef))

  def extractArgumentsFromPath(actionDef: Action) = {
    val rx = """\{([a-zA-Z0-9_]+)\}*""".r
    rx.findAllIn(actionDef.href).map(_.replaceAll("\\{", "").replaceAll("\\}", "")).map(name => name -> (PARAM(name, "String").tree)).toSeq
  }

  def reqJson(modelJson: ResourceDef, actionsDefs: List[Action]) = {
    val modelToJsons = flattenActions(actionsDefs).map {
      a =>
        a.title match {
          case "Create" => Some(toJson(modelJson.title, s"models.Create${modelJson.title}Body"))
          case "List" => None
          case "Info" => None
          case "Update" => Some(toJson(modelJson.title, s"models.Update${modelJson.title}Body"))
          case "Delete" => None
          case _ => None
        }
    }.flatten
    TRAITDEF(s"${modelJson.title}RequestJson") := BLOCK(
      modelToJsons.toSeq
    )
  }

  def respJson(modelJson: ResourceDef, actionsDefs: List[Action]) = {
    TRAITDEF(s"${modelJson.title}ResponseJson") := BLOCK(fromJson(modelJson.title, modelJson.title))
  }

  val schemaObj = SchemaModel.schemaObj

  object Typez {
    def mapPropTypeInfo[T](properties: Map[String, Either[NestedDef, FieldDefinition]], funk: (String, FieldDefinition) => T) = {
      val keys = properties.keySet
      keys.map {
        k =>
          val prop = properties(k)
          prop.right.map(
            typ => funk(k, typ)
          ).fold(r => None, x => Some(x))
      }.flatten
    }

    def mapSchemaPropTypeInfo[T](properties: Map[String, Either[OneOf, FieldDefinition]], funk: (String, FieldDefinition) => T) = {
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

  case class Schema(properties: Map[String, Either[OneOf, FieldDefinition]]) {
    def mapSchemaPropTypeInfo[T](funk: (String, FieldDefinition) => T) = Typez.mapSchemaPropTypeInfo(properties, funk)
  }

  case class Action(title: String, rel: String, href: String, method: String, schema: Option[Schema]) {
    def mapPropTypeInfo[T](funk: (String, FieldDefinition) => T) = schema.map {
      s => s.mapSchemaPropTypeInfo(funk)
    }.getOrElse(Seq.empty[T])
  }

  case class FieldDefinition(description: String, example: Option[JsValue], format: Option[String], readOnly: Option[Boolean], `type`: List[String])

  case class NestedDef(properties: Map[String, FieldDefinition])

  case class ResourceDef(title: String, description: String, properties: Map[String, Either[NestedDef, FieldDefinition]], links: Option[List[Action]]) {
    def mapPropTypeInfo[T](funk: (String, FieldDefinition) => T) = Typez.mapPropTypeInfo(properties, funk)
  }

  case class SchemaDoc(properties: Map[String, ResourceDef])

  case class OneOf(oneOf: List[FieldDefinition])

  object SchemaModel {

    implicit def re[L, R](implicit l: Reads[L], r: Reads[R]): Reads[Either[L, R]] = Reads(
      js => r.reads(js).map(Right(_)).orElse(l.reads(js).map(Left(_)))
    )

    implicit lazy val fd = Json.format[FieldDefinition]
    implicit lazy val fo = Json.format[OneOf]
    implicit lazy val fn = Json.format[NestedDef]
    implicit lazy val rs = Json.reads[Schema]
    implicit lazy val ra = Json.reads[Action]

    implicit lazy val fr = Json.reads[ResourceDef]
    implicit lazy val fs = Json.reads[SchemaDoc]

    def schemaObj = {
      //This is kinda hacky I think but seems to work. 3 passes.
      //Once for definitions, once for links and properties in definitions, once for root properties?
      val root = Json.parse(schema).as[JsObject]
      val defs = (root \ "definitions").as[JsObject].fields.map {
        case (model, defs: JsObject) => (model, expandRefs(root, defs))
      }
      val defRoot = Json.obj("definitions" -> JsObject(defs))

      val defProps = (defRoot \ "definitions").as[JsObject].fields.map {
        case (model, defs: JsObject) => (model, expandRefs(defRoot, defs))
      }

      val schemaRoot = (root - "definitions") + ("definitions" -> JsObject(defProps))

      val schemaProps = (schemaRoot \ "properties").as[JsObject].fields.map {
        case (model, defs: JsObject) => (model, expandRefs(schemaRoot, defs))
      }

      val expanded = (schemaRoot - "properties") + ("properties" -> JsObject(schemaProps))
      //and collapse 3 times lol
      val one = collapseExpandedRefs(expanded, expanded)
      val two = collapseExpandedRefs(one.as[JsObject], one)

      val noDefs = collapseExpandedRefs(two.as[JsObject], two).as[JsObject] - "definitions"
      noDefs
      dropPropDefs(noDefs).as[JsObject]
    }

    def dropPropDefs(js: JsValue): JsValue = js match {
      case o: JsObject => JsObject((o - "definitions").fields.map(f => (f._1 -> dropPropDefs(f._2))))
      case x @ _ => x
    }

    def schemaFile = new File("api/src/main/resources/schema.json")

    //System.err.println(schemaFile.getAbsolutePath)

    def schema = Source.fromFile(schemaFile).foldLeft(new StringBuilder) {
      case (b, c) => b.append(c)
    }.toString

    def expandRefs(root: JsObject, js: JsValue): JsValue = js match {
      case o: JsObject => {
        val jsObject: JsObject = JsObject(o.fields.map {
          case ("$ref", s: JsString) => getRef(root, s.value)
          case (name, field) => (name, expandRefs(root, field))
        })
        jsObject
      }
      case a: JsArray => JsArray(a.value.map(expandRefs(root, _)))
      case x @ _ => x
    }

    def getRef(node: JsObject, ref: String): (String, JsValue) = {
      val paths: Array[String] = ref.substring("#/".length).split("/")
      val resolved = paths.foldLeft(node) {
        (obj, path) =>
          (obj \ path).asInstanceOf[JsObject]
      }
      ("$ref", resolved)
    }

    def collapseExpandedRefs(root: JsObject, js: JsValue): JsValue = js match {
      case o: JsObject => {
        val jsObject: JsObject = JsObject(o.fields.foldLeft(Seq.empty[(String, JsValue)]) {
          case (seq, ("$ref", expanded: JsObject)) => dedupe(seq, expanded.fields)
          case (seq, (name, field)) => seq ++ Seq(name -> collapseExpandedRefs(root, field))
        })
        jsObject
      }
      case a: JsArray => JsArray(a.value.map(collapseExpandedRefs(root, _)))
      case x @ _ => x
    }

    def dedupe(one: Seq[(String, JsValue)], two: Seq[(String, JsValue)]): Seq[(String, JsValue)] = {
      val oneKeys = one.map(_._1).toSet
      one ++ two.filter(kv => !oneKeys.contains(kv._1))
    }

  }

  println(Json.prettyPrint(SchemaModel.schemaObj))
  println(SchemaModel.schemaObj.as[SchemaDoc])
  codez.foreach(c => println(treeToString(c)))

  object NewSchool {

    implicit def fmtResource: Format[Resource] = Json.format[Resource]

    implicit def fmtAction: Format[Link] = Json.format[Link]

    implicit def fmtSchema: Format[Schema] = Json.format[Schema]

    implicit def re[L, R](implicit l: Format[L], r: Format[R]): Format[Either[L, R]] = Format(Reads(
      js =>
        JsSuccess(js.validate[R].fold({
          er =>
            js.validate[L].fold({ el => sys.error(er.toString + el.toString + Json.prettyPrint(js)) }, { s => Left[L, R](s) })
        }, {
          r => Right[L, R](r)
        }))
    ), Writes {
      case Right(ar) => r.writes(ar)
      case Left(al) => l.writes(al)
    })

    implicit def fd: Format[FieldDefinition] = Json.format[FieldDefinition]

    implicit def fr: Format[Ref] = Json.format[Ref]

    implicit def fo: Format[OneOf] = Json.format[OneOf]

    implicit def fn: Format[NestedDef] = Json.format[NestedDef]

    implicit def fmtRootSchema: Format[RootSchema] = Json.format[RootSchema]

    def fileToString(schemaFile: String) = Source.fromFile(schemaFile).foldLeft(new StringBuilder) {
      case (b, c) => b.append(c)
    }.toString

    def schemaText(name: String): String = {
      val schemaFile = s"api/src/main/resources/schema/$name.json"
      fileToString(schemaFile)
    }

    //$ref :  #definitions/something or schema/foo#definitions/something
    case class Ref(`$ref`: String) {
      def path = `$ref`

      def isLocal: Boolean = path.startsWith("#")

      def schema: Option[String] = if (isLocal) None
      else Some(path.substring(0, path.indexOf("#")))

      def definition: String = path.substring("#/definitions/".length)
    }

    //these are the fields on either a top level or inner object or schema, which hang off definitions and are resolved by $ref
    case class FieldDefinition(description: String, example: Option[JsValue], format: Option[String], readOnly: Option[Boolean], `type`: List[String])

    //these map to "inner" objects inside a top level object, like region inside app
    case class NestedDef(properties: Map[String, Ref])

    //Describes the body of a PUT/POST in a Link
    case class Schema(properties: Map[String, Either[Either[OneOf, Ref], FieldDefinition]])

    //Endpoint
    case class Link(title: String, rel: String, href: String, method: String, schema: Option[Schema]) {
      val refEx = """\{(.+)\}""".r
      val encEx = """\((.+)\)""".r

      def extractHrefParams(implicit root: RootSchema, res: Resource): Seq[String] = {
        href.split('/').map {
          //decode urlencoded $refs in the href
          case refEx(encEx(ref)) => Some(Ref(URLDecoder.decode(ref))).map(r => res.resolveFieldRef(r).fold(o => r.schema.getOrElse(title) + o.orFields, f => r.schema.getOrElse(title) + r.definition))
          case refEx(ref) => Some(Ref(ref)).map(r => res.resolveFieldRef(r).fold(o => o.orFields, f => r.definition))
          case _ => None
        }.toSeq.flatten
      }
    }

    //in our case it is either the id or friendly id so should be size 2
    case class OneOf(oneOf: List[Ref]) {
      def orFields = oneOf match {
        case one :: two :: Nil => s"${one.definition}_or_${two.definition}"
        case _ => sys.error(s"OneOf had ${oneOf.length} items. Expected 2")
      }
    }

    //schema for a endpoint/object type
    case class Resource(description: String, id: String, title: String, definitions: Map[String, Either[OneOf, FieldDefinition]], links: List[Link], properties: Map[String, Either[NestedDef, Ref]]) {
      def resolveFieldRef(ref: Ref)(implicit root: RootSchema): Either[OneOf, FieldDefinition] = {
        ref.schema.map(resource => root.resource(resource)).getOrElse(this).definitions(ref.definition)
      }
    }

    //root schema.json
    case class RootSchema(description: String, properties: Map[String, Map[String, String]], title: String) {

      val resourceMap = new collection.mutable.HashMap[String, Resource]

      def resources = properties.keys

      def resource(name: String): Resource = resourceMap.getOrElseUpdate(name, loadResource(name))

      def loadResource(name: String): Resource = {
        val ref = properties(name)("$ref")
        // "/schema/app#"
        val schema = ref.drop("/schema".length).dropRight("#".length)
        val text = schemaText(schema)
        Json.parse(text).validate[Resource].fold(
          { e => sys.error(e.toString) },
          { res => res }
        )
      }

      def loadAll = resources.map(resource)

    }

    def loadRoot = Json.parse(fileToString("api/src/main/resources/schema.json")).as[RootSchema]
  }

}
