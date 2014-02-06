import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play4jpa-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    "play4jpa" % "play4jpa_2.10" % "1.0-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Local Play Repository" at "file://usr/local/Cellar/play/2.2.1/libexec/repository/local"
  )

}
