import bintray.Keys._

organization := "net.iakovlev"
name := "sbt-sonar-scoverage"
sbtPlugin := true
version := "0.1-SNAPSHOT"
description := "SBT plugin to publish multi-project build coverage to SonarQube"
licenses += ("MIT", url("http://opensource.org/licenses/mit-license.php"))
publishMavenStyle := false
repository in bintray := "sbt-plugins"
bintrayOrganization in bintray := None

bintrayPublishSettings

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

libraryDependencies += "org.sonarsource.scanner.api" % "sonar-scanner-api" % "2.8"
//libraryDependencies += "org.codehaus.sonar.runner" % "sonar-runner-dist" % "2.4"

ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value,
  // .jvmopts is ignored, simulate here
  "-XX:MaxPermSize=256m",
  "-Xmx2g",
  "-Xss2m",
  "-Dsonar.host.url=http://localhost:9000"
)