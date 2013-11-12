
import java.io.{ FileWriter, File }
import java.net.URLDecoder
import play.api.libs.json._
import scala.io.Source
import treehugger.forest._
import definitions._
import treehuggerDSL._

/*
this generates scala source for schema.json
the names of the files are printed to standard out so sbt knows to compile them
if you need debug logging use the method `e` which prints to standard err instead.
*/
object ModelBoilerplateGen extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
    val Request = RootClass.newClass("Request")
    val RequestWithBody = RootClass.newClass("RequestWithBody")
    val RequestWithEmptyResponse = RootClass.newClass("RequestWithEmptyResponse")
    val ListRequest = RootClass.newClass("ListRequest")
  }

  def api = {
    implicit val root = loadRoot
    /*for each non empty resource generate the scala source*/
    root.resources.values.filter(_.nonEmpty).map {
      resource =>
        resource.name -> (BLOCK(Seq(
          IMPORT("com.heroku.platform.api.Request._"),
          IMPORT(s"${resource.name}._"),
          companion(resource, root) withDoc (resource.description),
          model(resource) withDoc (resource.description)) ++
          Seq(reqJson(resource).map(_ withDoc (s"json serializers related to ${resource.name}")),
            respJson(resource).map(_ withDoc (s"json deserializers related to ${resource.name}"))).flatten
        ).inPackage(sym.ApiPackage): Tree)
    }
  }

  /*
  model case class per resource
   */
  def model(resource: Resource)(implicit root: RootSchema) = {
    val params = resource.properties.map {
      case (k, Right(ref)) =>
        val typ = resource.resolveFieldRef(ref).fold({
          oneOf => sys.error("Not expecting oneOf")
        }, {
          fieldDef => fieldType(k, fieldDef)(resource)
        })
        (PARAM(k, typ).tree)
      case (k, Left(nestedDef)) if resource.id == "schema/app" && k == "stack" => /*Hack, fix*/
        (PARAM(k, TYPE_OPTION("String")).tree)
      case (k, Left(nestedDef)) if nestedDef.optional =>
        (PARAM(k, TYPE_OPTION("models." + resource.name + Resource.camelify(initialCap(k)))).tree)
      case (k, Left(nestedDef)) =>
        (PARAM(k, TYPE_REF("models." + resource.name + Resource.camelify(initialCap(k)))).tree)
    }
    (CASECLASSDEF(resource.name) withParams params: Tree)

  }

  /*
  companion object that holds on to the action case classes and
  a nested models object that have req body case classes and nested values in the model
   */
  def companion(implicit resource: Resource, root: RootSchema) = {
    val name: String = resource.name

    val actionCaseClasses = resource.links.map {
      link =>
        val paramsMap = link.schema.map {
          schema =>
            schema.properties.flatMap {
              case (k, typ) =>
                typ match {
                  case Right(Right(fieldDef)) => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                  case Right(Left(nestedDef)) => nestedDef.properties.flatMap {
                    case (nk, nref) =>
                      resource.resolveFieldRef(nref).fold({
                        oneOf => argsFromAnyOf(k, oneOf, schema.isRequired(k))
                      }, {
                        fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                      })
                  }
                  case Left(Right(ref)) =>
                    resource.resolveFieldRef(ref).fold({
                      oneOf => argsFromAnyOf(k, oneOf, schema.isRequired(k))
                    }, {
                      fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                    })
                  case Left(Left(oneOf)) => argsFromAnyOf(k, oneOf, schema.isRequired(k))
                }

            }
        }.getOrElse(Seq.empty[(String, ValDef)])

        val hrefParamNames: Seq[String] = link.extractHrefParams
        val hrefParams = hrefParamNames.map(name => name -> (PARAM(name, StringClass)).tree)

        val params = (hrefParams ++ paramsMap).toSeq.map(_._2)
        val paramNames = paramsMap.toSeq.map(_._1)
        val extra = extraParams(link)

        val req = link.rel match {
          case "destroy" if resource.id == "schema/dyno" => requestWithEmptyResponse(resource, paramNames, params, extra, link, hrefParamNames)
          case "self" | "delete" | "destroy" => request(resource, paramNames, params, extra, link, hrefParamNames)
          case "instances" => listRequest(resource, paramNames, params, extra, link, hrefParamNames)
          case "create" | "update" => requestWithBody(resource, paramNames, params, extra, link, hrefParamNames)
          case x => sys.error("======> UNKNOWN link.rel:" + x)
        }
        (req: Tree) withDoc (link.description)
    }

    val bodyCaseClasses = resource.links.map {
      link =>
        link.rel match {
          case "create" | "update" => Some(bodyCaseClass(link))
          case _ => None
        }
    }.flatten

    OBJECTDEF(name) := BLOCK(Seq(IMPORT(s"${resource.name}.models._")) ++
      Seq((OBJECTDEF("models") := BLOCK(bodyCaseClasses ++ nestedModelClasses))) ++ actionCaseClasses
    )
  }

  def argsFromFieldDef(k: String, fieldDef: FieldDefinition, required: Boolean)(implicit resource: Resource, root: RootSchema): Seq[(String, ValDef)] = {
    Seq(if (required) (k -> (PARAM(k, requiredArg(k, fieldDef))))
    else (k -> (PARAM(k, argType(k, fieldDef)) := NONE)))
  }

  /*
   e.g the app_id_or_name and recipient_email_or_id in
   case class Create(app_id_or_name: String, recipient_email_or_id: String)
   */
  def argsFromAnyOf(k: String, oo: AnyOf, required: Boolean)(implicit resource: Resource, root: RootSchema): Seq[(String, ValDef)] = {
    val field = s"${k}_${oo.orFields}"
    Seq(if (required) (field -> (PARAM(field, TYPE_REF("String"))))
    else (field -> (PARAM(field, TYPE_OPTION("String")) := NONE)))
  }

  /*
  e.g the app and recipient in
  case class CreateAppTransferBody(app: String, recipient: String)
   */
  def bodyArgsFromAnyOf(k: String, oo: AnyOf, required: Boolean)(implicit resource: Resource, root: RootSchema): Seq[(String, ValDef)] = {
    Seq(if (required) (k -> (PARAM(k, TYPE_REF("String"))))
    else (k -> (PARAM(k, TYPE_OPTION("String")) := NONE)))
  }

  /*
  extra params for requests. range for list reqs
   */
  def extraParams(link: Link): Seq[ValDef] = {
    val defs: Seq[ValDef] = if (link.rel == "instances") {
      Seq((PARAM("range", TYPE_OPTION("String")) := NONE))
    } else Seq.empty[ValDef]
    defs
  }

  /*
  nested model case classes e.g AppRegion inside App
   */
  def nestedModelClasses(implicit resource: Resource, root: RootSchema) = {
    resource.properties.map {
      case (k, Right(ref)) => None
      case (k, Left(nestedDef)) => Some {
        val params = nestedDef.properties.map {
          case (name, ref) =>
            val typ = resource.resolveFieldRef(ref).fold({
              oneOf => sys.error("Not expecting oneOf")
            }, {
              fieldDef => fieldType(name, fieldDef)
            })
            (PARAM(name, typ): ValDef)
        }
        ((CASECLASSDEF(resource.name + Resource.camelify(initialCap(k))) withParams params): Tree)
      }
    }.flatten
  }

  /*
  body for Create and Update calls
  */
  def bodyCaseClass(link: Link)(implicit resource: Resource, root: RootSchema) = {
    val params = link.schema.map {
      schema =>
        schema.properties.flatMap {
          case (k, typ) =>
            typ match {
              case Right(Right(fieldDef)) => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
              case Right(Left(nestedDef)) => nestedDef.properties.flatMap {
                case (nk, nref) =>
                  resource.resolveFieldRef(nref).fold({
                    oneOf => argsFromAnyOf(k, oneOf, schema.isRequired(k))
                  }, {
                    fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                  })
              }
              case Left(Right(ref)) =>
                resource.resolveFieldRef(ref).fold({
                  oneOf => bodyArgsFromAnyOf(k, oneOf, schema.isRequired(k))
                }, {
                  fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                })
              case Left(Left(oneOf)) => argsFromAnyOf(k, oneOf, schema.isRequired(k))
            }

        }
    }.getOrElse(Seq.empty[(String, ValDef)]).map(_._2)

    ((CASECLASSDEF(s"${link.action}${resource.name}Body") withParams params.toIterable): Tree)
  }

  /*
  s"${resource.name}RequestJson" trait, holds the ToJson for the resource
   */
  def reqJson(resource: Resource)(implicit root: RootSchema): Option[Tree] = {

    val modelToJsons = resource.links.map {
      link =>
        link.rel match {
          case "create" | "update" =>
            val to = s"${link.action}${resource.name}Body"
            Some(toJson(to, s"models.${to}"))
          case _ => None
        }
    }.flatten

    val nesteds = resource.properties.map {
      case (k, Right(ref)) => None
      case (k, Left(nestedDef)) =>
        Some(toJson(resource.name + Resource.camelify(initialCap(k)), "models." + resource.name + Resource.camelify(initialCap(k))))
    }.flatten

    val toJsons = modelToJsons.toSeq ++ nesteds.toSeq

    if (toJsons.isEmpty) None
    else Some(TRAITDEF(s"${resource.name}RequestJson") := BLOCK(toJsons))
  }

  /*
   s"${resource.name}ResponseJson" trait, holds the FromJson for the resource
  */
  def respJson(resource: Resource)(implicit root: RootSchema): Option[Tree] = {
    val resps = resource.properties.map {
      case (k, Right(ref)) => None
      case (k, Left(nestedDef)) =>
        Some(fromJson(resource.name + Resource.camelify(initialCap(k)), "models." + resource.name + Resource.camelify(initialCap(k))))
    }.flatten ++ Seq(fromJson(resource.name, resource.name), fromJson(s"List${resource.name}", s"collection.immutable.List[${resource.name}]"))
    if (resps.isEmpty) None
    else Some(TRAITDEF(s"${resource.name}ResponseJson") := BLOCK(resps))
  }

  /* implicit def ToJson*s and FromJson*s */

  def toJson(model: String, typ: String) = {
    DEF("ToJson" + model, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT)
  }

  def fromJson(model: String, typ: String) = {
    DEF("FromJson" + model, sym.FromJson TYPE_OF typ) withFlags (Flags.IMPLICIT)
  }

  /*request case classes*/

  def request(resource: Resource, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], link: Link, hrefParams: Seq[String]) = {
    if ((params ++ extra).isEmpty)
      (CASEOBJECTDEF(link.action) withParents (sym.Request TYPE_OF (resource.name)) := BLOCK(
        expect("expect200"), endpoint(link.href, hrefParams), method(link.method.toUpperCase)): Tree)
    else
      (CASECLASSDEF(link.action) withParams params ++ extra withParents (sym.Request TYPE_OF (resource.name)) := BLOCK(
        expect("expect200"), endpoint(link.href, hrefParams), method(link.method.toUpperCase)): Tree)
  }

  def requestWithBody(resource: Resource, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], link: Link, hrefParams: Seq[String]) = {
    val exp = if (link.rel == "create") "expect201" else "expect200"
    (CASECLASSDEF(link.action) withParams params ++ extra withParents (sym.RequestWithBody TYPE_OF (s"models.${link.action}${resource.name}Body", resource.name)) := BLOCK(
      expect(exp), endpoint(link.href, hrefParams), method(link.method.toUpperCase),
      (VAL("body", s"models.${link.action}${resource.name}Body") := (REF(s"models.${link.action}${resource.name}Body") APPLY (paramNames.map(REF(_))))
      )): Tree)
  }

  def listRequest(resource: Resource, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], link: Link, hrefParams: Seq[String]) = {
    (CASECLASSDEF(link.action) withParams params ++ extra withParents (sym.ListRequest TYPE_OF (resource.name)) := BLOCK(
      endpoint(link.href, hrefParams), method(link.method.toUpperCase),
      (DEF("nextRequest", (sym.ListRequest TYPE_OF (resource.name))) withParams ((VAL("nextRange", "String"))) := THIS DOT "copy" APPLY (REF("range") := SOME(REF("nextRange"))))))
  }

  def requestWithEmptyResponse(resource: Resource, paramNames: Iterable[String], params: Iterable[ValDef], extra: Iterable[ValDef], link: Link, hrefParams: Seq[String]) = {
    (CASECLASSDEF(link.action) withParams params ++ extra withParents (sym.RequestWithEmptyResponse) := BLOCK(
      expect("expect202"), endpoint(link.href, hrefParams), method(link.method.toUpperCase)): Tree)
  }

  def expect(exRef: String) = (VAL("expect", TYPE_SET(IntClass)) := REF(exRef))

  def endpoint(endRef: String, params: Seq[String]) = {
    val endLit = endRef.replaceAll("""\{.+?\}""", """\%s""")
    if (params.isEmpty) (VAL("endpoint", StringClass) := LIT(endLit))
    else (VAL("endpoint", StringClass) := LIT(endLit) DOT "format" APPLY (params.toSeq.map(p => REF(p)): _*))
  }

  def method(methRef: String) = (VAL("method", StringClass) := REF(methRef))

  /* util */

  def initialCap(s: String) = {
    val (f, l) = s.splitAt(1)
    s"${
      f.toUpperCase
    }$l"
  }

  def e(a: AnyRef) = System.err.println(a)

  def fieldType(name: String, fieldDef: FieldDefinition)(implicit resource: Resource) = {
    val typ = fieldDef.`type`
    val isOptional = typ.contains("null")
    if (isOptional) {
      argType(name, fieldDef)
    } else {
      requiredArg(name, fieldDef)
    }
  }

  def argType(name: String, fieldDef: FieldDefinition)(implicit resource: Resource) = specialCase(resource, name).getOrElse {
    val typez = convertTypes(fieldDef.`type`)
    if (typez.length == 1) {
      fieldDef.items.map {
        items =>
          (TYPE_OPTION(initialCap(typez(0))) TYPE_OF (initialCap(items.`type`)))
      }.getOrElse {
        (TYPE_OPTION(initialCap(typez(0))))
      }
    } else {
      throw new IllegalStateException("encountered type with more than one non null type value")
    }
  }

  def requiredArg(name: String, fieldDef: FieldDefinition)(implicit resource: Resource) = specialCase(resource, name).getOrElse {
    val typez = convertTypes(fieldDef.`type`)
    if (typez.length == 1) {
      fieldDef.items.map {
        items =>
          (TYPE_REF(initialCap(typez(0))) TYPE_OF (initialCap(items.`type`)))
      }.getOrElse {
        (TYPE_REF(initialCap(typez(0))))
      }
    } else {
      throw new IllegalStateException("encountered type with more than one non null type value")
    }
  }

  /*
  special case handling of fields that this generator cant deal with yet, or where there are inconsistencies btw doc and api behavior (stack)
  */
  def specialCase(resource: Resource, field: String) = {
    (resource.id, field) match {
      case ("schema/dyno", "env") => Some((TYPE_OPTION(TYPE_MAP("String", "String"))))
      case ("schema/slug", "process_types") => Some((TYPE_MAP("String", "String")))
      case ("schema/slug", "blob") => Some((TYPE_MAP("String", "String")))
      case ("schema/oauth-token", "client") => Some((TYPE_REF("OAuthTokenClient")))
      case ("schema/oauth-token", "grant") => Some((TYPE_REF("OAuthTokenGrant")))
      case ("schema/oauth-token", "refresh_token") => Some((TYPE_REF("OAuthTokenRefreshToken")))
      case ("schema/app", "stack") => Some((TYPE_OPTION("String")))
      case ("schema/log-drain", "addon") => Some((TYPE_OPTION("String")))
      case ("schema/addon", "config") => Some((TYPE_OPTION(TYPE_MAP("String", "String"))))
      case _ => None
    }
  }

  def convertTypes(types: List[String]) = {
    types.filter(_ != "null").map {
      case "integer" => "Int"
      case x => x
    }
  }

  def aggReqJson(implicit root: RootSchema): List[String] = {
    root.resources.values.filter(_.nonEmpty).toList.sortBy(_.name).map {
      resource =>
        reqJson(resource).map(t => resource.name + "RequestJson")
    }.flatten
  }

  def aggRespJson(implicit root: RootSchema): List[String] = {
    root.resources.values.filter(_.nonEmpty).toList.sortBy(_.name).map {
      resource =>
        respJson(resource).map(t => resource.name + "ResponseJson")
    }.flatten
  }

  def reqJson(implicit root: RootSchema) = {
    (TRAITDEF("ApiRequestJson") withParents ("ConfigVarRequestJson" :: aggReqJson): Tree)
  }

  def respJson(implicit root: RootSchema) = {
    (TRAITDEF("ApiResponseJson") withParents ("ErrorResponseJson" :: "ConfigVarResponseJson" :: aggRespJson): Tree)
  }

  def apiJson(implicit root: RootSchema) = (BLOCK(reqJson, respJson).inPackage(sym.ApiPackage): Tree)

  /*schema.json parsing*/
  implicit def fmtAI: Format[ArrayItems] = Json.format[ArrayItems]

  implicit def fmtResource: Format[Resource] = Json.format[Resource]

  implicit def fmtAction: Format[Link] = Json.format[Link]

  implicit def fmtSchema: Format[Schema] = Json.format[Schema]

  implicit def re[L, R](implicit l: Format[L], r: Format[R]): Format[Either[L, R]] = Format(Reads(
    js =>
      js.validate[R].fold({
        er =>
          js.validate[L].fold({
            el => JsError(er.toString + el.toString + Json.prettyPrint(js))
          }, {
            s => JsSuccess(Left[L, R](s))
          })
      }, {
        r => JsSuccess(Right[L, R](r))
      })
  ), Writes {
    case Right(ar) => r.writes(ar)
    case Left(al) => l.writes(al)
  })

  implicit def fd: Format[FieldDefinition] = Json.format[FieldDefinition]

  implicit def fr: Format[Ref] = Json.format[Ref]

  implicit def fo: Format[AnyOf] = Json.format[AnyOf]

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
  // new single file
  // #/definitions/account-feature/definitions/id
  case class Ref(`$ref`: String) {
    def path = `$ref`

    def schema: Option[String] = Some(path.split('/')(2))

    def definition: String = path.split('/')(4)
  }

  case class ArrayItems(`type`: String)
  //these are the fields on either a top level or inner object or schema, which hang off definitions and are resolved by $ref
  case class FieldDefinition(description: Option[String], example: Option[JsValue], format: Option[String], readOnly: Option[Boolean], `type`: List[String], items: Option[ArrayItems])

  //these map to "inner" objects inside a top level object, like region inside app
  case class NestedDef(properties: Map[String, Ref], `type`: List[String]) {
    val optional = `type`.contains("null")
  }

  //Describes the body of a PUT/POST in a Link
  case class Schema(properties: Map[String, Either[Either[AnyOf, Ref], Either[NestedDef, FieldDefinition]]], required: Option[List[String]]) {
    def isRequired(field: String) = required.exists(_.contains(field))
  }

  //Endpoint
  case class Link(description: String, title: String, rel: String, href: String, method: String, schema: Option[Schema]) {
    val refEx = """\{(.+)\}""".r
    val encEx = """\((.+)\)""".r

    def extractHrefParams(implicit root: RootSchema, res: Resource): Seq[String] = {
      href.split('/').map {
        //decode urlencoded $refs in the href
        case refEx(encEx(ref)) => Some(Ref(URLDecoder.decode(ref))).map {
          r =>
            res.resolveFieldRef(r).fold(
              o => r.schema.map(_.replace("-", "_")).getOrElse(res.nameForParam.toLowerCase) + "_" + o.orFields,
              f => r.schema.map(s => s.replace("-", "_")).getOrElse(res.nameForParam) + "_" + r.definition)
        }
        case refEx(ref) => Some(Ref(ref)).map(r => res.resolveFieldRef(r).fold(o => o.orFields, f => r.definition))
        case _ => None
      }.toSeq.flatten
    }
    def action: String = {
      Resource.camelify(title.replace(" ", "_"))
    }
  }

  //in our case it is either the id or friendly id so should be size 2
  case class AnyOf(anyOf: List[Ref]) {
    def orFields = anyOf match {
      case one :: two :: Nil => s"${one.definition}_or_${two.definition}"
      case one :: Nil => s"${one.definition}"
      case _ => sys.error(s"OneOf had ${anyOf.length} items. Expected 1 or 2")
    }
  }

  object Resource {
    //Lift  StringHelpers
    def camelify(name: String): String = {
      def loop(x: List[Char]): List[Char] = (x: @unchecked) match {
        case '_' :: '_' :: rest => loop('_' :: rest)
        case '-' :: '-' :: rest => loop('-' :: rest)
        case '-' :: c :: rest => Character.toUpperCase(c) :: loop(rest)
        case '_' :: c :: rest => Character.toUpperCase(c) :: loop(rest)
        case '_' :: Nil => Nil
        case '-' :: Nil => Nil
        case c :: rest => c :: loop(rest)
        case Nil => Nil
      }
      if (name == null)
        ""
      else
        loop('_' :: name.toList).mkString
    }

    def camelifyMethod(name: String): String = {
      val tmp: String = camelify(name)
      if (tmp.length == 0)
        ""
      else
        tmp.substring(0, 1).toLowerCase + tmp.substring(1)
    }
  }

  //schema for a endpoint/object type
  case class Resource(description: String, id: String, title: String, definitions: Map[String, Either[AnyOf, FieldDefinition]], links: List[Link], properties: Map[String, Either[NestedDef, Ref]]) {
    def resolveFieldRef(ref: Ref)(implicit root: RootSchema): Either[AnyOf, FieldDefinition] = {
      val res: Resource = ref.schema.map(resource => root.resource(resource)).getOrElse(this)
      res.definitions.get(ref.definition).getOrElse(sys.error(s"cant resolve ${ref} -> ${ref.definition} from $res"))
    }

    def camelify(name: String) = Resource.camelify(name)
    def camelifyMethod(name: String) = Resource.camelifyMethod(name)

    lazy val name = {
      camelify(initialCap(id.drop("schema/".length)) match {
        case "App" => "HerokuApp"
        case x => x.replace("Oauth", "OAuth")
      })
    }

    lazy val nameForParam = {
      (initialCap(id.drop("schema/".length)) match {
        case "App" => "HerokuApp"
        case x => x.replace("Oauth", "OAuth")
      }).replace('-', '_')
    }

    def hasModel = !properties.isEmpty

    def hasLinks = !links.isEmpty

    def nonEmpty = hasModel || hasLinks

  }

  //root schema.json
  case class RootSchema(description: String, properties: Map[String, Map[String, String]], title: String, definitions: Map[String, Resource]) {

    val byHand = Set("config-var")

    def resources = definitions.filter(kv => !(byHand.contains(kv._1)))

    def resource(name: String): Resource = definitions(name)

  }

  /*drop config vars since they use the funky patternProperties*/
  val pruneConfigVars = (__ \ "definitions" \ "config-var").json.prune

  def loadRoot = Json.parse(fileToString("api/src/main/resources/schema.json")).transform(pruneConfigVars).get.as[RootSchema]

  def writeFile(dir: File, fileName: String, tree: String) = {
    val resFile = new File(dir, fileName)
    resFile.delete()
    val w = new FileWriter(resFile)
    w.write(tree)
    w.close()
    println(resFile.getPath)
  }

  def generate(dir: File) = {
    api.map {
      case (resource, resTree) =>
        writeFile(dir, s"${resource}.scala", treeToString(resTree))
    }

    writeFile(dir, s"ApiJson.scala", treeToString(apiJson(loadRoot)))

  }

  {
    val outDir = new File(args(0))
    generate(outDir)
  }
}
