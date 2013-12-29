import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play4jpa"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaJpa,
    "org.hibernate" % "hibernate-entitymanager" % "4.2.7.Final"
  )

  lazy val fixy = play.Project(
    appName + "-fixy",
    appVersion,
    appDependencies,
    path = file("module/fixy")

  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  ).dependsOn(fixy).aggregate(fixy)

}
