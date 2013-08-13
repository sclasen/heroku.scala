import com.heroku.platform.api._
import java.lang.reflect._
import java.lang.reflect.{ Type => JType }

import scala.Some
import scala.Some
import scala.Some
import scala.Some
import treehugger.forest._
import definitions._
import treehuggerDSL._

object PlayJsonBoilerplateGen extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api._"
    val ClientPackage = "com.heroku.platform.api.client.spray"
    val PlayPackage = "play.api.libs.json._"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
    val JsonFormat = RootClass.newClass("Format")
    val JsonReads = RootClass.newClass("Reads")
    val JsonWrites = RootClass.newClass("Writes")
    val BoilerplateObj = "PlayJsonBoilerplate"
  }

  val respJson = classOf[ApiResponseJson]

  val reqJson = classOf[ApiRequestJson]

  val jsonBoilerplate = BLOCK(
    IMPORT(sym.ApiPackage),
    IMPORT(sym.PlayPackage),
    playJson
  ).inPackage(sym.ClientPackage)

  def playJson =
    //object SprayApiJson handles null attributes in Json by giving back None
    OBJECTDEF(sym.BoilerplateObj) withParents ("ApiRequestJson", "ApiResponseJson") := BLOCK(
      respJson.getMethods.filter(m => m.getReturnType == classOf[FromJson[_]])
        .map {
          m =>
            jsonReads(m)
        }.toSeq.flatten ++
        reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]])
        .map {
          m =>
            jsonWrites(m)
        }.toSeq.flatten ++
        reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]])
        .map {
          m => toJson(m)
        } ++
        respJson.getMethods.filter(m => m.getReturnType == classOf[FromJson[_]])
        .map {
          m => fromJson(m)
        } ++ Seq(from, to)
    )

  def to = {
    /*
      Json.prettyPrint(Json.toJson(t))
    */
    (DEF("to") withTypeParams (TYPEVAR("T")) withParams (PARAM("f", sym.JsonWrites TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.ToJson TYPE_OF "T") := BLOCK(
        (DEF("toJson") withParams (PARAM("t", "T"))) := (REF("Json") DOT "prettyPrint" APPLY (REF("Json") DOT "toJson" APPLY REF("t")))
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
    (DEF("from") withTypeParams (TYPEVAR("T")) withParams (PARAM("t", sym.JsonReads TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.FromJson TYPE_OF "T") := BLOCK(
        (DEF("fromJson") withParams (PARAM("json", "String"))) := ((REF("Json") DOT "parse" APPLY REF("json")) DOT "as" APPLYTYPE ("T"))
      )): Tree)
  }

  def importz(m: Method) = {

  }

  def toJson(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    // implicit lazy val methodFromApiRequestJson : ToJson[CorrectType] = to[CorrectType]
    (LAZYVAL(m.getName, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("to") APPLYTYPE (typ): Tree)
  }

  def fromJson(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    // implicit lazy val methodFromApiResponseJson: FromJson[CorrectType] = from[CorrectType]
    (LAZYVAL(m.getName, sym.FromJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("from") APPLYTYPE (typ): Tree)
  }

  def jsonReads(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    if (t.isInstanceOf[Class[_]]) {
      //val arity = t.asInstanceOf[Class[_]].getConstructors.apply(0).getParameterTypes.size
      //implicit lazy val Format<SomeType> = jsonFormat<SomeType.arity>(SomeType.apply)
      Some(LAZYVAL(readsName(typ), sym.JsonReads TYPE_OF typ) withFlags (Flags.IMPLICIT) := (BLOCK(IMPORT("com.heroku.platform.api." + typ), REF("Json.reads") APPLYTYPE typ: Tree)))
    } else {
      None
    }
  }

  def jsonWrites(m: Method) = {
    val t = getTypeParamOfReturnType(m)
    val typ = getTypeParamForSource(t)
    if (t.isInstanceOf[Class[_]]) {
      //val arity = t.asInstanceOf[Class[_]].getConstructors.apply(0).getParameterTypes.size
      //implicit lazy val Format<SomeType> = jsonFormat<SomeType.arity>(SomeType.apply)
      Some(LAZYVAL(writesName(typ), sym.JsonWrites TYPE_OF typ) withFlags (Flags.IMPLICIT) := (BLOCK(IMPORT("com.heroku.platform.api." + typ), REF("Json.writes") APPLYTYPE typ: Tree)))
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
      var raw = pt.getRawType.asInstanceOf[Class[_]].getSimpleName
      val params = pt.getActualTypeArguments.map(t => t.asInstanceOf[Class[_]].getSimpleName).reduceLeft(_ + "," + _)
      if (raw == "List") raw = "collection.immutable.List"
      s"$raw[$params]"
    }
  }

  def e(a: AnyRef) = System.err.println(a)

  def formatName(typ: String) = {
    "Format" + typ.replaceAll("\\$", "").replaceAll("\\.", "")
  }
  def readsName(typ: String) = {
    "Reads" + typ.replaceAll("\\$", "").replaceAll("\\.", "")
  }

  def writesName(typ: String) = {
    "Writes" + typ.replaceAll("\\$", "").replaceAll("\\.", "")
  }

  println(treeToString(jsonBoilerplate))

}
