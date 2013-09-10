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


  val modelBoilerplateGen = Project(
    id = "model-boilerplate-generator",
    base = file("boilerplate-generator/model"),
    settings = buildSettings ++ Seq(libraryDependencies ++= Seq(treehugger, playJson))
  )

  val api = Project(
    id = "api",
    base = file("api"),
    settings = buildSettings ++ Seq(libraryDependencies ++= apiDeps)
  ).settings(generateModelBoilerplate:_*)


  val jsonBoilerplateGen = Project(
    id = "json-boilerplate-generator",
    base = file("boilerplate-generator/json"),
    settings = buildSettings ++ Seq(libraryDependencies ++= Seq(treehugger, sprayJson))
  ).dependsOn(api)

  val spray_client = Project(
    id = "spray-client",
    base = file("spray-client"),
    dependencies = Seq(api % "it->test;test->test;compile->compile"),
    settings = buildSettings ++ Seq(libraryDependencies ++= sprayDeps)
  ).settings(Defaults.itSettings: _*).configs(IntegrationTest).settings(generateJsonBoilerplate:_*)

  lazy val jsonBoilerplate = TaskKey[Seq[File]]("json-boilerplate", "Generate Json Boilerplate")

  lazy val generateJsonBoilerplate:Seq[Project.Setting[_]] = Seq(
    sourceGenerators in Compile <+= (jsonBoilerplate in Compile).task,
    sourceManaged in Compile <<= baseDirectory / "src_managed/main/scala",
    jsonBoilerplate in Compile <<= (cacheDirectory, sourceManaged in Compile, dependencyClasspath in Runtime in jsonBoilerplateGen, compile in api in Compile, streams) map {
      (cacheDir, sm, cp, apiComp, st) =>
        val apiClasses = apiComp.relations.allProducts
        val cache =
          FileFunction.cached(cacheDir / "json-boilerplate-generator", inStyle = FilesInfo.hash, outStyle = FilesInfo.hash) {
            in: Set[File] =>
              genJsonBoilerplate(sm / "com/heroku/platform/api/client/spray/SprayJsonBoilerplate.scala", cp.files, "SprayJsonBoilerplateGen", st) ++
                genJsonBoilerplate(sm / "com/heroku/platform/api/client/spray/PlayJsonBoilerplate.scala", cp.files, "PlayJsonBoilerplateGen", st)
          }

       cache(apiClasses.toSet).toSeq
    }
  )

  lazy val modelBoilerplate = TaskKey[Seq[File]]("model-boilerplate", "Generate Model Boilerplate")

  lazy val generateModelBoilerplate:Seq[Project.Setting[_]] = Seq(
    sourceGenerators in Compile <+= (modelBoilerplate in Compile).task,
    sourceManaged in Compile <<= baseDirectory / "src_managed/main/scala",
    modelBoilerplate in Compile <<= (cacheDirectory, sourceManaged in Compile, dependencyClasspath in Runtime in modelBoilerplateGen, baseDirectory, streams) map {
      (cacheDir, sm, cp, base, st) =>
       val schema = Set(base / "src/main/resources/schema.json")
        genModelboilerplate(sm / "com/heroku/platform/api/model", cp.files, "ModelBoilerplateGen", st).toSeq

       /* val cache =
         FileFunction.cached(cacheDir / "model-boilerplate-generator", inStyle = FilesInfo.hash, outStyle = FilesInfo.hash) {
           in: Set[File] =>
              genModelboilerplate(sm / "com/heroku/platform/api/model", cp.files, "ModelBoilerplateGen", st)
         }

        cache(schema).toSeq*/
    }
  )


  def genModelboilerplate(source: File, cp: Seq[File], mainClass:String, streams:Types.Id[Keys.TaskStreams]): Set[File] = {
    streams.log.info("Generating:%s".format(source))
    val baos = new ByteArrayOutputStream()
    val i = new Fork.ForkScala(mainClass).fork(None, Nil, cp, Seq(source.getAbsolutePath), None, false, CustomOutput(baos)).exitValue()
    if (i != 0) {
      streams.log.error("Trouble with code generator")
    }
    val files = new String(baos.toByteArray).split('\n').map(new File(_))
    Set(files:_*)
  }

  def genJsonBoilerplate(source: File, cp: Seq[File], mainClass:String, streams:Types.Id[Keys.TaskStreams]): Set[File] = {
    streams.log.info("Generating:%s".format(source))
    val baos = new ByteArrayOutputStream()
    val i = new Fork.ForkScala(mainClass).fork(None, Nil, cp, Nil, None, false, CustomOutput(baos)).exitValue()
    if (i != 0) {
      streams.log.error("Trouble with code generator")
    }
    val code = new String(baos.toByteArray)
    IO delete source
    IO write(source, code)
    Set(source)
  }


  val root = Project(id = "heroku-scala-project", base = file("."), settings = buildSettings).aggregate(modelBoilerplateGen, api, jsonBoilerplateGen, spray_client)

  def apiDeps = Seq(scalaTest)

  def sprayDeps = Seq(spray, sprayJson % "provided", akka, scalaTest, playJson % "provided")



  val spray = "io.spray" % "spray-client" % "1.2-M8" % "compile"
  val sprayJson = "io.spray" %% "spray-json" % "1.2.5"
  val akka = "com.typesafe.akka" %% "akka-actor" % "2.2.0" % "compile"
  val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  val treehugger = "com.eed3si9n" %% "treehugger" % "0.3.0"
  val playJson = "com.typesafe.play" %% "play-json" % "2.2.0-M2"

}
