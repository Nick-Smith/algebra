import sbt._

object ConciergePlugins extends Build {

  lazy val plugins = Project(
    id = "content-api-concierge-plugins",
    base = file(".")
  )
  .dependsOn(
    uri("git://github.com/guardian/sbt-teamcity-test-reporting-plugin.git#v1.5"),
    uri("git://github.com/guardian/sbt-version-info-plugin.git#v2.7")
  )
  .settings(
    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.1")
  )

}
