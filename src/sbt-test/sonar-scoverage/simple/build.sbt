scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.7" % "test"
)

fork in Test := true

lazy val root = (project in file(".")).aggregate(child1, child2)
lazy val localRoot = (project in file("local")).aggregate(child1, child2)
lazy val child1 = (project in file("local/children/child1")).settings(commonSettings)
lazy val child2 = (project in file("local/children/child2")).settings(commonSettings)

sonarProjectKey := "AcsMono"
sonarProjectName := "Acs Mono Repository"
sonarProjectVersion := "1.0"
sonarRunnerOptions := List("-e", "-X")
