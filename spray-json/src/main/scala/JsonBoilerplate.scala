import com.heroku.platform.api.{ FromJson, ToJson, ApiRequestJson, ApiResponseJson }
import java.lang.reflect._
import java.lang.reflect.{ Type => JType }

import treehugger.forest._
import definitions._
import treehuggerDSL._

object JsonBoilerplate extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api._"
    val ClientPackage = "com.heroku.platform.api.client.spray"
    val SprayPackage = "spray.json._"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
    val JsonFormat = RootClass.newClass("JsonFormat")
  }

  val respJson = classOf[ApiResponseJson]

  val reqJson = classOf[ApiRequestJson]

  val jsonBoilerplate = BLOCK(
    IMPORT(sym.ApiPackage),
    IMPORT(sym.SprayPackage),
    ignoreNulls,
    sprayJson
  ).inPackage(sym.ClientPackage)

  def ignoreNulls =
    //object SprayIgnoreNullJson omits Options that are None instead of sending null
    OBJECTDEF("SprayIgnoreNullJson") withParents ("DefaultJsonProtocol", "ApiRequestJson") := BLOCK(
      Seq(nullSafeConfigToJson, configToJson) ++
        reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]])
        .map {
          m => jsonFormat(m)
        }.toSeq.flatten ++
        reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]] && m.getName != "configToJson")
        .map {
          m => toJson(m)
        } ++ Seq(to)
    )

  def sprayJson =
    //object SprayApiJson handles null attributes in Json by giving back None
    OBJECTDEF("SprayApiJson") withParents ("DefaultJsonProtocol", "NullOptions", "ApiRequestJson", "ApiResponseJson") := BLOCK(
      Seq(apiConfigToJson) ++
        respJson.getMethods.filter(m => m.getReturnType == classOf[FromJson[_]])
        .map {
          m =>
            jsonFormat(m)
        }.toSeq.flatten ++
        reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]] && m.getName != "configToJson")
        .map {
          m => callToJson(m)
        } ++
        respJson.getMethods.filter(m => m.getReturnType == classOf[FromJson[_]])
        .map {
          m => fromJson(m)
        } ++ Seq(from)
    )

  def to = {
    /*
      def to[T](implicit f: JsonFormat[T]) = new ToJson[T] {
        def toJson(t: T): String = t.toJson.compactPrint
      }
    */
    (DEF("to") withTypeParams (TYPEVAR("T")) withParams (PARAM("f", sym.JsonFormat TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.ToJson TYPE_OF "T") := BLOCK(
        (DEF("toJson") withParams (PARAM("t", "T"))) := REF("t") DOT "toJson" DOT "compactPrint"
      )): Tree)
  }

  def from = {
    /*
      def from[T](implicit f: JsonFormat[T]) = new FromJson[T] {
       def fromJson(json: String): T = try {
         JsonParser(json).convertTo[T]
       } catch {
         case d: DeserializationException =>
           println(json)
           throw d
       }
      }
    */
    (DEF("from") withTypeParams (TYPEVAR("T")) withParams (PARAM("t", sym.JsonFormat TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.FromJson TYPE_OF "T") := BLOCK(
        (DEF("fromJson") withParams (PARAM("json", "String"))) := (TRY(REF("JsonParser") APPLY REF("json") DOT "convertTo" APPLYTYPE "T")
          CATCH (
            CASE(ID("e") withType ("DeserializationException")) ==> BLOCK((
              Predef_println APPLY REF("json")), THROW("DeserializationException", REF("e")))
          ) ENDTRY)
      )): Tree)
  }

  def toJson(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    // implicit lazy val methodFromApiRequestJson : ToJson[CorrectType] = to[CorrectType]
    (LAZYVAL(m.getName, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("to") APPLYTYPE (typ): Tree)
  }

  def callToJson(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    // implicit lazy val  methodFromApiRequestJson = SprayIgnoreNullJson.methodFromApiRequestJson
    (LAZYVAL(m.getName, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("SprayIgnoreNullJson") DOT m.getName: Tree)
  }

  def fromJson(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    // implicit lazy val methodFromApiResponseJson: FromJson[CorrectType] = from[CorrectType]
    (LAZYVAL(m.getName, sym.FromJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("from") APPLYTYPE (typ): Tree)
  }

  def jsonFormat(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    if (t.isInstanceOf[Class[_]]) {
      val arity = t.asInstanceOf[Class[_]].getConstructors.apply(0).getParameterTypes.size
      //implicit lazy val Format<SomeType> = jsonFormat<SomeType.arity>(SomeType.apply)
      Some((LAZYVAL(formatName(typ), sym.JsonFormat TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF(s"jsonFormat$arity") APPLY (REF(typ) DOT "apply"): Tree))
    } else {
      None
    }
  }

  def getTypeParamOfReturnType(m: Method): JType = m.getGenericReturnType.asInstanceOf[ParameterizedType].getActualTypeArguments.apply(0)

  def getArity(t: JType) = t.asInstanceOf[Class[_]].getConstructors.apply(0).getParameterTypes.size

  def getTypeParamForSource(t: JType): String = {
    if (t.isInstanceOf[Class[_]]) {
      val name = t.asInstanceOf[Class[_]].getName
      name.substring(name.lastIndexOf('.') + 1).replace('$', '.')
    } else {
      val pt = t.asInstanceOf[ParameterizedType]
      val raw = pt.getRawType.asInstanceOf[Class[_]].getSimpleName
      val params = pt.getActualTypeArguments.map(t => t.asInstanceOf[Class[_]].getSimpleName).reduceLeft(_ + "," + _)
      s"$raw[$params]"
    }
  }

  def e(a: AnyRef) = System.err.println(a)

  def formatName(typ: String) = {
    "Format" + typ.replaceAll("\\$", "").replaceAll("\\.", "")
  }

  def nullSafeConfigToJson = {
    /*
     implicit val nullSafeConfigToJson: ToJson[Map[String, Option[String]]] = to[Map[String, Option[String]]]
    */
    (LAZYVAL("nullSafeConfigToJson", sym.ToJson TYPE_OF TYPE_MAP("String", TYPE_OPTION("String")))
      withFlags (Flags.IMPLICIT) :=
      REF("to") APPLYTYPE (TYPE_MAP("String", TYPE_OPTION("String"))): Tree)
  }

  def configToJson = {
    /*
     implicit val configToJson: ToJson[Map[String, String]] = new ToJson[Map[String, String]] {
       def toJson(t: Map[String, String]): String = {
         nullSafeConfigToJson.toJson(t.map {
           case (k, v) => k -> Option(v)
         })
       }
     }
    */
    (LAZYVAL("configToJson", sym.ToJson TYPE_OF TYPE_MAP("String", "String"))
      withFlags (Flags.IMPLICIT) := NEW(ANONDEF(sym.ToJson TYPE_OF TYPE_MAP("String", "String")) := BLOCK(
        (DEF("toJson") withParams (PARAM("t", TYPE_MAP("String", "String")))) := REF("nullSafeConfigToJson") DOT "toJson" APPLY (
          REF("t") MAP LAMBDA(PARAM("kv")) ==> BLOCK(REF("kv._1") ANY_-> REF("Option") APPLY REF("kv._2"))
        )
      )): Tree)
  }

  def apiConfigToJson = {
    (LAZYVAL("configToJson", sym.ToJson TYPE_OF TYPE_MAP("String", "String"))
      withFlags (Flags.IMPLICIT) := REF("SprayIgnoreNullJson") DOT "configToJson": Tree)
  }

  println(treeToString(jsonBoilerplate))

}
