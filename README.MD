# SBT + Sonar + Scoverage

## Quote
> If you can't manage it, you can't improve it.
>
>― *attributed to different people*

## Overview
Enables SonarQube analysis on SBT projects. Supports aggregated SBT projects as multi-module SonarQube analysis, 
and Scoverage statements reporting.  

## Usage
Add dependency to your project:

In `project/plugins.sbt`:
```scala
addSbtPlugin("net.iakovlev" % "sbt-sonar-scoverage" % "0.1.0")
```

The plugin is automatically enabled for projects with Scoverage plugin enabled. To enable Scoverage plugin, use
```scala
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0") //or later version
```

## Copyright and License

All code is licensed under the MIT license, available at
http://opensource.org/licenses/mit-license.php and also in the COPYING
file.
