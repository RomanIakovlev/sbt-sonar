addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
addSbtPlugin("net.iakovlev" % "sbt-sonar-scoverage" % System.getProperty("plugin.version"))

libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0"