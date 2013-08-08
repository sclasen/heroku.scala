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
    settings = buildSettings ++ Seq(libraryDependencies ++= Seq(reflect,treehugger))
  ).dependsOn(api)

  val spray_client = Project(
    id = "spray-client",
    base = file("spray-client"),
    settings = buildSettings ++ Seq(libraryDependencies ++= sprayDeps)
  ).settings( Defaults.itSettings : _*).configs( IntegrationTest )
    .dependsOn(api % "it->test;test->test;compile->compile")


  val root = Project(id = "heroku-scala-project", base = file("."), settings = buildSettings).aggregate(api, spray_client)


  def apiDeps = Seq(scalaTest)

  def sprayDeps = Seq(spray, sprayJson, akka, scalaTest)


  val spray = "io.spray" % "spray-client" % "1.2-20130801" % "compile"
  val sprayJson = "io.spray" %% "spray-json" % "1.2.5"
  val akka = "com.typesafe.akka" %% "akka-actor" % "2.2.0" % "compile"
  val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  val treehugger =  "com.eed3si9n" %% "treehugger" % "0.2.3"
  val reflect =  "org.scala-lang" % "scala-reflect" % "2.10.0"

}
