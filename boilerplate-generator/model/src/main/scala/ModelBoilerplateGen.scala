
import java.io.{ FileWriter, File }
import java.net.URLDecoder
import play.api.libs.json._
import scala.io.Source
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
  }

  def api = {
    implicit val root = loadRoot
    root.resources.map(root.resource).map {
      resource =>
        val ids: Seq[Tree] = Seq(resourceIdentity(resource),
          resourceIdentityCompanion(resource)).flatten
        resource.name -> (BLOCK(
          Seq(
            IMPORT("com.heroku.platform.api.Request._"),
            IMPORT(s"${resource.name}._"),
            companion(resource, root),
            model(resource)) ++
            ids ++
            Seq(
              reqJson(resource),
              respJson(resource))
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
      case (k, Left(nestedDef)) if resource.id == "schema/app" && k == "stack" =>
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
                        oneOf => argsFromOneOf(k, oneOf, schema.isRequired(k))
                      }, {
                        fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                      })
                  }
                  case Left(Right(ref)) =>
                    resource.resolveFieldRef(ref).fold({
                      oneOf => identityFromOneOf(k, ref, schema.isRequired(k))
                    }, {
                      fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                    })
                  case Left(Left(oneOf)) => sys.error("unused")
                }

            }
        }.getOrElse(Seq.empty[(String, ValDef)])

        val hrefParamNames: Seq[String] = link.extractHrefParams
        val hrefParams = hrefParamNames.map(name => name -> (PARAM(name, StringClass)).tree)

        val params = (hrefParams ++ paramsMap).toSeq.map(_._2)
        val paramNames = paramsMap.toSeq.map(_._1)
        val extra = extraParams(link)

        link.rel match {
          case "self" | "delete" | "destroy" => request(resource, paramNames, params, extra, link, hrefParamNames)
          case "instances" => listRequest(resource, paramNames, params, extra, link, hrefParamNames)
          case "create" | "update" => requestWithBody(resource, paramNames, params, extra, link, hrefParamNames)
          case x => sys.error("======> UNKNOWN link.rel:" + x)
        }
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
    Seq(if (required || (k == "grant" && resource.id == "schema/oauth-token")) (k -> (PARAM(k, requiredArg(k, fieldDef))))
    else (k -> (PARAM(k, argType(k, fieldDef)) := NONE)))
  }

  def argsFromOneOf(k: String, oo: OneOf, required: Boolean)(implicit resource: Resource, root: RootSchema): Seq[(String, ValDef)] = {
    Seq(if (required) (k -> (PARAM(k, requiredArg(k, hollowFieldDef(List(resource.name + initialCap(k)))))))
    else (k -> (PARAM(k, argType(k, hollowFieldDef(List(resource.name + initialCap(k))))) := NONE)))
  }

  def identityFromOneOf(k: String, ref: Ref, required: Boolean)(implicit resource: Resource, root: RootSchema): Seq[(String, ValDef)] = {
    val res = Resource.camelify(ref.schema.map {
      schema =>
        schema.replace("oauth", "OAuth")
    }.map {
      case "app" => "HerokuApp"
      case x => x
    }.get)
    val typ = s"${res}Identity"
    if (required)
      Seq((k -> (PARAM(k, requiredArg(k, hollowFieldDef(List(typ)))))))
    else
      Seq((k -> (PARAM(k, argType(k, hollowFieldDef(List(typ, "null")))) := NONE)))

  }

  def hollowFieldDef(typez: List[String]): FieldDefinition = FieldDefinition(None, None, None, None, typez, None)

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
            //Hack, fix later.
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
                    oneOf => argsFromOneOf(k, oneOf, schema.isRequired(k))
                  }, {
                    fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                  })
              }
              case Left(Right(ref)) =>
                resource.resolveFieldRef(ref).fold({
                  oneOf => identityFromOneOf(k, ref, schema.isRequired(k))
                }, {
                  fieldDef => argsFromFieldDef(k, fieldDef, schema.isRequired(k))
                })
              case Left(Left(oneOf)) => sys.error("unused")
            }

        }
    }.getOrElse(Seq.empty[(String, ValDef)]).map(_._2)

    ((CASECLASSDEF(s"${link.action}${resource.name}Body") withParams params.toIterable): Tree)
  }

  /*

  case class CollaboratorIdentity private [api](email:Option[String],id:Option[String])
object CollaboratorIdentity{
  def byEmail(email:String) = CollaboratorIdentity(Some(email),None)
  def byId(id:String) = CollaboratorIdentity(None,Some(id))
}
   */

  def resourceIdentity(resource: Resource)(implicit root: RootSchema): Option[Tree] = {
    resource.definitions.get("identity").flatMap {
      id =>
        id match {
          case Left(oneOf) =>
            val params = oneOf.oneOf.map {
              one => (PARAM(one.definition, TYPE_OPTION("String")): ValDef)
            }
            Some((CASECLASSDEF(s"${initialCap(resource.name)}Identity") withParams params.toIterable): Tree)
          case _ => None
        }
    }
  }

  def resourceIdentityCompanion(resource: Resource)(implicit root: RootSchema): Option[Tree] = {
    resource.definitions.get("identity").flatMap {
      id =>
        id match {
          case Left(oneOf) =>
            val defs = oneOf.oneOf match {
              case one :: two :: Nil =>
                Seq((DEF(s"by${initialCap(one.definition)}") withParams (PARAM(one.definition, "String"))) :=
                  REF(s"${initialCap(resource.name)}Identity") APPLY Seq((REF("Some") APPLY REF(one.definition)), REF("None")),
                  (DEF(s"by${initialCap(two.definition)}") withParams (PARAM(two.definition, "String"))) :=
                    REF(s"${initialCap(resource.name)}Identity") APPLY Seq(REF("None"), (REF("Some") APPLY REF(two.definition)))
                )

              case one :: Nil => Seq((DEF(s"by${initialCap(one.definition)}") withParams (PARAM(one.definition, "String"))) :=
                REF(s"${initialCap(resource.name)}Identity") APPLY Seq((REF("Some") APPLY REF(one.definition)))
              )
            }

            Some((CASEOBJECTDEF(s"${initialCap(resource.name)}Identity") := BLOCK(defs)): Tree)
          case _ => None
        }
    }
  }

  /*
  s"${resource.name}RequestJson" trait, holds the ToJson for the resource
   */
  def reqJson(resource: Resource)(implicit root: RootSchema) = {

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

    val id = resource.definitions.get("identity").map(_ => toJson(s"${initialCap(resource.name)}Identity", s"${initialCap(resource.name)}Identity"))

    TRAITDEF(s"${resource.name}RequestJson") := BLOCK(
      modelToJsons.toSeq ++ nesteds.toSeq ++ id.toSeq
    )
  }

  /*
   s"${resource.name}ResponseJson" trait, holds the FromJson for the resource
  */
  def respJson(resource: Resource)(implicit root: RootSchema) = {
    val resps = resource.properties.map {
      case (k, Right(ref)) => None
      case (k, Left(nestedDef)) =>
        Some(fromJson(resource.name + Resource.camelify(initialCap(k)), "models." + resource.name + Resource.camelify(initialCap(k))))
    }.flatten ++ Seq(fromJson(resource.name, resource.name), fromJson(s"List${resource.name}", s"collection.immutable.List[${resource.name}]"))
    TRAITDEF(s"${resource.name}ResponseJson") := BLOCK(resps)
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

  def fieldType(name: String, fieldDef: FieldDefinition)(implicit resource: Resource) = specialCase(resource, name).getOrElse {
    val typ = fieldDef.`type`
    val isOptional = typ.contains("null")
    val typez = convertTypes(fieldDef.`type`)
    if (typez.length == 1) {
      if (isOptional) (TYPE_OPTION(initialCap(typez(0))))
      else (TYPE_REF(initialCap(typez(0))))
    } else {
      throw new IllegalStateException("encountered type with more than one non null type value")
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

  def specialCase(resource: Resource, field: String) = {
    (resource.id, field) match {
      case ("schema/dyno", "env") => Some((TYPE_OPTION(TYPE_MAP("String", "String"))))
      case ("schema/oauth-token", "client") => Some((TYPE_OPTION("OAuthTokenClient")))
      case ("schema/oauth-token", "grant") => Some((TYPE_REF("OAuthTokenGrant")))
      case ("schema/oauth-token", "refresh_token") => Some((TYPE_OPTION("OAuthTokenRefreshToken")))
      case ("schema/app", "stack") => Some((TYPE_OPTION("String")))
      case _ => None
    }
  }

  def convertTypes(types: List[String]) = {
    types.filter(_ != "null").map {
      case "integer" => "Int"
      case x => x
    }
  }

  def aggJson(suffix: String)(implicit root: RootSchema) = {
    root.resources.map(root.resource).filter(_.hasModel).toList.sortBy(_.name).map {
      resource =>
        resource.name + suffix
    }
  }

  def reqJson(implicit root: RootSchema) = {
    (TRAITDEF("ApiRequestJson") withParents ("ConfigVarRequestJson" :: "AddonRequestJson" :: "LogDrainRequestJson" :: aggJson("RequestJson")): Tree)
  }

  def respJson(implicit root: RootSchema) = {
    (TRAITDEF("ApiResponseJson") withParents ("ErrorResponseJson" :: "ConfigVarResponseJson" :: "AddonResponseJson" :: "LogDrainResponseJson" :: aggJson("ResponseJson")): Tree)
  }

  def apiJson(implicit root: RootSchema) = (BLOCK(IMPORT("com.heroku.platform.api._"), reqJson, respJson).inPackage(sym.ApiPackage): Tree)
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
  case class Schema(properties: Map[String, Either[Either[OneOf, Ref], Either[NestedDef, FieldDefinition]]], required: Option[List[String]]) {
    def isRequired(field: String) = required.exists(_.contains(field))
  }

  //Endpoint
  case class Link(title: String, rel: String, href: String, method: String, schema: Option[Schema]) {
    val refEx = """\{(.+)\}""".r
    val encEx = """\((.+)\)""".r

    def extractHrefParams(implicit root: RootSchema, res: Resource): Seq[String] = {
      href.split('/').map {
        //decode urlencoded $refs in the href
        case refEx(encEx(ref)) => Some(Ref(URLDecoder.decode(ref))).map(r => res.resolveFieldRef(r).fold(o => r.schema.map(_.replace("-", "_")).getOrElse(res.nameForParam.toLowerCase) + "_" + o.orFields, f => r.schema.map(s => s.replace("-", "_")).getOrElse(res.nameForParam) + "_" + r.definition))
        case refEx(ref) => Some(Ref(ref)).map(r => res.resolveFieldRef(r).fold(o => o.orFields, f => r.definition))
        case _ => None
      }.toSeq.flatten
    }
    def action: String = {
      Resource.camelify(title.replace(" ", "_"))
    }
  }

  //in our case it is either the id or friendly id so should be size 2
  case class OneOf(oneOf: List[Ref]) {
    def orFields = oneOf match {
      case one :: two :: Nil => s"${one.definition}_or_${two.definition}"
      case one :: Nil => s"${one.definition}"
      case _ => sys.error(s"OneOf had ${oneOf.length} items. Expected 1 or 2")
    }
  }

  object Resource {
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
  case class Resource(description: String, id: String, title: String, definitions: Map[String, Either[OneOf, FieldDefinition]], links: List[Link], properties: Map[String, Either[NestedDef, Ref]]) {
    def resolveFieldRef(ref: Ref)(implicit root: RootSchema): Either[OneOf, FieldDefinition] = {
      val res: Resource = ref.schema.map(resource => root.resource(resource)).getOrElse(this)
      res.definitions.get(ref.definition).getOrElse(sys.error(s"cant resolve ${ref} -> ${ref.definition} from $res"))
    }
    //Lift  StringHelpers

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
  }

  //root schema.json
  case class RootSchema(description: String, properties: Map[String, Map[String, String]], title: String, definitions: Map[String, Resource]) {

    val byHand = Set("config-var", "addon", "log-drain")

    val resourceMap = new collection.mutable.HashMap[String, Resource]

    def resources = properties.keys.filter(k => !(byHand.contains(k)))

    def resource(name: String): Resource = resourceMap.getOrElseUpdate(name, loadResource(name))

    def loadResource(name: String): Resource = {
      definitions(name)
    }

    def loadAll = resources.map(resource)

  }

  def loadRoot = Json.parse(fileToString("api/src/main/resources/schema.json")).as[RootSchema]

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

  /*api.foreach{
    t =>
      println(treeToString(t))
  }
*/
}
