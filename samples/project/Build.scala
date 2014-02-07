import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play4jpa-sample"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    "play4jpa" % "play4jpa_2.10" % "0.1-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  )

}
