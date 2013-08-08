import java.io.ByteArrayOutputStream
import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

object Build extends Build {

  val buildSettings = Seq(
    organization := "com.heroku.api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.10.2",
    crossScalaVersions := Seq("2.10.2"),
    resolvers ++= Seq(
      "TypesafeMaven" at "http://repo.typesafe.com/typesafe/maven-releases",
      "whydoineedthis" at "http://repo.typesafe.com/typesafe/releases",
      "spray repo" at "http://repo.spray.io",
      "spray nightlies" at "http://nightlies.spray.io/",
      "sonatype" at "https://oss.sonatype.org/content/groups/public")
  ) ++ Defaults.defaultSettings ++ scalariformSettings

  val api = Project(
    id = "api",
    base = file("api"),
    settings = buildSettings ++ Seq(libraryDependencies ++= apiDeps)
  )

  val spray_json = Project(
    id = "spray-json",
    base = file("spray-json"),
    settings = buildSettings ++ Seq(libraryDependencies ++= Seq(treehugger))
  ).dependsOn(api)

  val spray_client = Project(
    id = "spray-client",
    base = file("spray-client"),
    dependencies = Seq(api % "it->test;test->test;compile->compile"), 
    settings = buildSettings ++ Seq(libraryDependencies ++= sprayDeps)
  ).settings(Defaults.itSettings: _*).configs(IntegrationTest).settings(generateJsonBoilerplate:_*)

  lazy val jsonBoilerplate = TaskKey[Seq[File]]("json-boilerplate", "Generate Spray Json Boilerplate")

  lazy val generateJsonBoilerplate:Seq[Project.Setting[_]] = Seq(
    sourceGenerators in Compile <+= (jsonBoilerplate in Compile).task,
    sourceManaged in Compile <<= baseDirectory / "src_managed/main/scala",
    jsonBoilerplate in Compile <<= (sourceManaged in Compile, dependencyClasspath in Runtime in spray_json, streams) map {
      (sm, cp, st) =>
        generate(sm / "com/heroku/platform/api/client/spray/SprayJsonBoilerplate.scala", cp.files, st)
    }
  )

  def generate(source: File, cp: Seq[File], streams:Types.Id[Keys.TaskStreams]): Seq[File] = {
    streams.log.info("Generating:%s".format(source))
    val mainClass = "JsonBoilerplate"
    val baos = new ByteArrayOutputStream()
    val i = new Fork.ForkScala(mainClass).fork(None, Nil, cp, Nil, None, false, CustomOutput(baos)).exitValue()
    if (i != 0) {
      streams.log.error("Trouble with code generator")
    }
    val code = new String(baos.toByteArray)
    IO delete source
    IO write(source, code)
    Seq(source)
  }


  val root = Project(id = "heroku-scala-project", base = file("."), settings = buildSettings).aggregate(api, spray_client)

  def apiDeps = Seq(scalaTest)

  def sprayDeps = Seq(spray, sprayJson, akka, scalaTest)

  val spray = "io.spray" % "spray-client" % "1.2-20130801" % "compile"
  val sprayJson = "io.spray" %% "spray-json" % "1.2.5"
  val akka = "com.typesafe.akka" %% "akka-actor" % "2.2.0" % "compile"
  val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  val treehugger = "com.eed3si9n" %% "treehugger" % "0.2.3"

}
