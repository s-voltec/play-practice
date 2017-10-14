name := """play-practice"""
organization := "pw.koakoa.play-practice"

version := "Play2.6_Slick3.2_1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// depencencies
libraryDependencies ++= Seq(
  evolutions,
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.1.2",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
)

// code generation
import better.files._
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick-codegen" % "3.2.0",
  "com.h2database" % "h2" % "1.4.196"
)
val pkg = "models.generated"
(sourceGenerators in Compile) += Def.taskDyn {
  val outputDirPath = ((sourceManaged in Compile).value / "slick").getPath
  val outputFilePath = (pkg + ".").replaceAllLiterally(".", java.io.File.separator) + "Tables.scala"
  val outputFile = file(outputDirPath) / outputFilePath

  val evolutionsFile = baseDirectory.value/"conf"/"evolutions"/"default"/"1.sql"
  val sqlFile = (resourceManaged in Compile).value/"slick"/"slick.sql"
  val updateRequired = evolutionsFile.exists && (!sqlFile.exists || (sqlFile.lastModified < evolutionsFile.lastModified))

  if(!updateRequired) { Def.task { Seq(outputFile) } }
  else {
    val profile = "slick.jdbc.H2Profile"
    val driver = "org.h2.Driver"
    val url = s"jdbc:h2:mem:;INIT=RUNSCRIPT FROM '${sqlFile.getPath}'"

    sqlFile.getParentFile.toScala.createDirectories()
    sqlFile.toScala.createIfNotExists()
    evolutionsFile.toScala.lines.foldLeft(false) { (state, line) =>
      if(state && !line.startsWith("#")) sqlFile.toScala.appendLines(line)
      if(line.startsWith("# --- !Ups")) true
      else if (line.startsWith("# --- !Downs")) false
      else state
    }

    Def.task {
      val cpFiles = (dependencyClasspath in Compile).value.files
      val log = streams.value.log
      (runner in Compile).value.run("slick.codegen.SourceCodeGenerator", cpFiles, Array(profile, driver, url, outputDirPath, pkg), log) match {
        case scala.util.Success(_) => Seq(outputFile)
        case scala.util.Failure(_) => sqlFile.delete(); sys.error("slick-codegen failed.")
      }
    }
  }
}

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "pw.koakoa.play-practice.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "pw.koakoa.play-practice.binders._"

