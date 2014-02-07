import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play4jpa"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaJpa,
    "org.hibernate" % "hibernate-entitymanager" % "4.2.7.Final"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  )

}
