import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

object AlgebraBuild extends Build {

  lazy val project = Project(
    id = "algebra",
    base = file(".")
  )
  .settings(
    scalaVersion := "2.11.2",
    scalacOptions ++= Seq("-feature", "-deprecation"),
    resolvers += "Guardian GitHub Repository" at "http://guardian.github.io/maven/repo-releases",
    resolvers += "Spray Repository" at "http://repo.spray.io",
    /* As PreviewSpec.scala makes a change to the configuration
       tests cannot be run in parallel */
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.2" % "test"
    ),
    // this library was renamed to jakata-regexp in 1.4, we don't want the old version
    ivyXML := <dependencies><exclude org="regexp" name="regexp" /></dependencies>
  )
  .settings(testOptions in Test += Tests.Argument("-oF"))

}
