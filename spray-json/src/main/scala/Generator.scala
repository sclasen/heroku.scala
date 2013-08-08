import com.heroku.platform.api.{ FromJson, ToJson, ApiRequestJson, ApiResponseJson }
import java.lang.reflect._
import scala.reflect.runtime.{ universe => ru }

import treehugger.forest._
import definitions._
import treehuggerDSL._

object Generator extends App {

  object sym {
    val ApiPackage = "com.heroku.platform.api._"
    val SprayPackage = "spray.json._"
    val ToJson = RootClass.newClass("ToJson")
    val FromJson = RootClass.newClass("FromJson")
  }

  val respJson = classOf[ApiResponseJson]

  val reqJson = classOf[ApiRequestJson]

  val mirror = ru.runtimeMirror(reqJson.getClassLoader)

  val ignoreNulls = OBJECTDEF("SprayIgnoreNullJson") withParents ("DefaultJsonProtocol", "ApiRequestJson") := BLOCK(
    Seq(IMPORT(sym.ApiPackage), IMPORT(sym.SprayPackage), to) ++
      //(DEF("to", sym.ToJson TYPE_OF sym.ToParam): Tree)
      reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]]).map {
        m => toJson(m)
      }
  )

  val sprayJson = OBJECTDEF("SprayApi") withParents ("DefaultJsonProtocol", "NullOptions", "ApiRequestJson") := BLOCK(
    Seq(IMPORT(sym.ApiPackage), IMPORT(sym.SprayPackage), from) ++
      //(DEF("to", sym.ToJson TYPE_OF sym.ToParam): Tree)
      reqJson.getMethods.filter(m => m.getReturnType == classOf[ToJson[_]]).map {
        m => toJsonFrom(m)
      } ++
      respJson.getMethods.filter(m => m.getReturnType == classOf[FromJson[_]]).map {
        m => fromJson(m)
      })
  /*
  def to[T](implicit f: JsonFormat[T]) = new ToJson[T] {
    def toJson(t: T): String = t.toJson.compactPrint
  }
  */
  def to = (
    DEF("to") withTypeParams (TYPEVAR("T")) withParams (PARAM("t", sym.ToJson TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.ToJson TYPE_OF "T") := BLOCK(
        (DEF("toJson") withParams (PARAM("t", sym.ToJson TYPE_OF "T"))) := REF("t") DOT "toJson" DOT "compactPrint"
      )): Tree)
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
  def from = (
    DEF("from") withTypeParams (TYPEVAR("T")) withParams (PARAM("t", sym.FromJson TYPE_OF "T") withFlags (Flags.IMPLICIT))
      := NEW(ANONDEF(sym.FromJson TYPE_OF "T") := BLOCK(
        (DEF("fromJson") withParams (PARAM("json", "String"))) := (TRY(REF("JsonParser") DOT "convertTo" APPLYTYPE "T")
          CATCH (
            CASE(ID("e") withType ("DeserializationException")) ==> (Predef_println APPLY REF("json"))
          ) ENDTRY)
      )): Tree)

  (DEF("maxList", "T")
    withTypeParams (TYPEVAR("T") VIEWBOUNDS TYPE_ORDERED("T"))
    withParams (PARAM("elements", TYPE_LIST("T"))): Tree)

  //def maxList[T <% Ordered[T]](elements: List[T]): T

  def toJson(m: Method) = {
    val t = m.getGenericReturnType.asInstanceOf[ParameterizedType].getActualTypeArguments.apply(0)
    val typ = if (t.isInstanceOf[Class[_]]) {
      val name = t.asInstanceOf[Class[_]].getName
      name.substring(name.lastIndexOf('.') + 1).replace('$', '.')
    } else {
      val name = t.asInstanceOf[ParameterizedType].toString
      name
      //name.substring(name.lastIndexOf('.') + 1).replace('<', '[').replace('>', ']').replace('$', '.')
    }

    (LAZYVAL(m.getName, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("to") APPLYTYPE (typ): Tree)
  }

  def toJsonFrom(m: Method) = {
    val t = m.getGenericReturnType.asInstanceOf[ParameterizedType].getActualTypeArguments.apply(0)
    val typ = if (t.isInstanceOf[Class[_]]) {
      val name = t.asInstanceOf[Class[_]].getName
      name.substring(name.lastIndexOf('.') + 1).replace('$', '.')
    } else {
      val name = t.asInstanceOf[ParameterizedType].toString
      name
      //name.substring(name.lastIndexOf('.') + 1).replace('<', '[').replace('>', ']').replace('$', '.')
    }

    (LAZYVAL(m.getName, sym.ToJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("SprayIgnoreNullJson") DOT m.getName: Tree)
  }

  def fromJson(m: Method) = {
    val t = m.getGenericReturnType.asInstanceOf[ParameterizedType].getActualTypeArguments.apply(0)
    val typ = if (t.isInstanceOf[Class[_]]) {
      val name = t.asInstanceOf[Class[_]].getName
      name.substring(name.lastIndexOf('.') + 1).replace('$', '.')
    } else {
      val name = t.asInstanceOf[ParameterizedType].toString
      name
      //name.substring(name.lastIndexOf('.') + 1).replace('<', '[').replace('>', ']').replace('$', '.')
    }

    (LAZYVAL(m.getName, sym.FromJson TYPE_OF typ) withFlags (Flags.IMPLICIT) := REF("from") APPLYTYPE (typ): Tree)
  }

  def toJsonFormat(m: Method) = {

  }

  def fromFormats(m: Method) = {

  }

  println(treeToString(ignoreNulls))
  println(treeToString(sprayJson))

}
