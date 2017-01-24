package net.iakovlev.sonar

import sbt._
import sbt.Keys._
import sbt.KeyRanks._
import scoverage.{ScoverageKeys, ScoverageSbtPlugin}
import java.nio.file.Paths
import java.nio.file.Files

import scala.collection.JavaConverters._
import java.io.{File, FileOutputStream}
import java.util.Properties

//import org.sonar.runner.{Main => SonarRunner}
import org.sonarsource.scanner.api.{EmbeddedScanner => SonarScanner}
import org.sonarsource.scanner.api.StdOutLogOutput

import scala.util.control.NonFatal

object SonarScoveragePlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = ScoverageSbtPlugin

  case class WithProject[T](ref: ProjectRef, value: T) {
    def map[V](f: T => V): WithProject[V] = copy(value = f(value))
  }

  object autoImport {
    lazy val scoverageReport: SettingKey[WithProject[String]] =
      settingKey[WithProject[String]]("Path to scoverage report")
    lazy val sonar: TaskKey[Unit] = taskKey[Unit]("Execute sonar runner on current project")
    lazy val sonarSources: SettingKey[WithProject[Seq[String]]] =
      settingKey[WithProject[Seq[String]]]("Paths to sources")
    lazy val sonarTests: SettingKey[WithProject[Seq[String]]]  = settingKey[WithProject[Seq[String]]]("Paths to tests")
    lazy val sonarBinaries: SettingKey[WithProject[String]]    = settingKey[WithProject[String]]("Paths to binaries")
    lazy val sonarProjectPath: SettingKey[WithProject[String]] = settingKey[WithProject[String]]("Project path")
    lazy val sonarProjectKey: SettingKey[String]               = settingKey[String]("SonarQube project key")
    lazy val sonarProjectName: SettingKey[String]              = settingKey[String]("SonarQube project name")
    lazy val sonarProjectVersion: SettingKey[String]           = settingKey[String]("SonarQube project version")
    lazy val sonarRunnerOptions: SettingKey[Seq[String]]       = settingKey[Seq[String]]("Sonar runner options")
  }

  import autoImport._

  def paths(key: SettingKey[Seq[File]]): Def.Initialize[WithProject[Seq[String]]] = {
    Def
      .setting {
        val seq      = key.value
        val absolute = Paths.get(baseDirectory.value.getAbsolutePath)
        WithProject(
          thisProjectRef.value,
          seq
            .map(p => Paths.get(p.getAbsolutePath))
            .filter(Files.exists(_))
            .map(absolute.relativize)
            .map(_.toString)
        )
      }
  }
  def path(key: SettingKey[File]): Def.Initialize[WithProject[String]] = {
    Def
      .setting {
        val p        = key.value
        val absolute = Paths.get(baseDirectory.value.getAbsolutePath)
        WithProject(thisProjectRef.value, absolute.relativize(Paths.get(p.getAbsolutePath)).toString)
      }
  }

  private val scopeFilter = SettingKey[ScopeFilter]("scopeFilter", "", Invisible)

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    sonarRunnerOptions := List.empty,
    sonarProjectKey := "",
    sonarProjectName := "",
    sonarProjectVersion := "",
    scopeFilter := Def
      .setting(ScopeFilter(inAggregates(thisProjectRef.value, includeRoot = thisProject.value.aggregate.isEmpty)))
      .value,
    sonarProjectPath := {
      val absolute = Def.setting(Paths.get((baseDirectory in ThisBuild).value.getAbsolutePath)).value
      val current  = Paths.get((baseDirectory in ThisProject).value.getAbsolutePath)
      WithProject(thisProjectRef.value, absolute.relativize(current).toString)
    },
    scoverageReport := path(crossTarget).value.map(p => Paths.get(p, "scoverage-report", "scoverage.xml").toString),
    sonarSources := paths(unmanagedSourceDirectories in Compile).value,
    sonarTests := paths(unmanagedSourceDirectories in Test).value,
    sonarBinaries := path(classDirectory in Compile).value,
    aggregate in sonar := false,
    sonar := {
      val log = streams.value.log
      log.info("Sonar has been started")
      val baseDir     = (baseDirectory in ThisBuild).value
      val allSources  = Def.settingDyn(sonarSources.all(scopeFilter.value)).value
      val allTests    = Def.settingDyn(sonarTests.all(scopeFilter.value)).value
      val allReports  = Def.settingDyn(scoverageReport.all(scopeFilter.value)).value
      val allPaths    = Def.settingDyn(sonarProjectPath.all(scopeFilter.value)).value
      val allBinaries = Def.settingDyn(sonarBinaries.all(scopeFilter.value)).value

      val misc = Map(
        "sonar.modules"        -> thisProject.value.aggregate.map(_.project).mkString(","),
        "sonar.projectHome"    -> baseDir.getAbsolutePath,
        "sonar.projectKey"     -> sonarProjectKey.value,
        "sonar.projectName"    -> sonarProjectName.value,
        "sonar.projectVersion" -> sonarProjectVersion.value,
        "sonar.language"       -> "scala"
      )
      val sources  = allSources.map(v => s"${v.ref.project}.sonar.sources" -> s"${v.value.mkString(",")}").toMap
      val tests    = allTests.map(v => s"${v.ref.project}.sonar.tests" -> s"${v.value.mkString(",")}").toMap
      val reports  = allReports.map(v => s"${v.ref.project}.sonar.scoverage.reportPath" -> s"${v.value}").toMap
      val paths    = allPaths.map(v => s"${v.ref.project}.sonar.projectBaseDir" -> s"${v.value}").toMap
      val binaries = allBinaries.map(v => s"${v.ref.project}.sonar.binaries" -> s"${v.value}").toMap
      val props    = sources ++ tests ++ reports ++ paths ++ binaries ++ misc

      val p = new Properties()
      p.putAll(props.asJava)
      try {
        val scanner = SonarScanner.create(new StdOutLogOutput)
        scanner.start()
        scanner.runAnalysis(p)
        scanner.stop()
        /*
        SonarRunner.main(props.map {
          case (k, v) =>
            s"-D$k=$v"
        }.toArray ++ sonarRunnerOptions.value)
       */
      } catch {
        case NonFatal(e) =>
          log.error("Sonar scanner exception: " + e.getMessage)
          e.printStackTrace()
        case e: Throwable =>
          log.error("Fatal exception: " + e.getMessage)
          e.printStackTrace()

      }
      log.info("Sonar is finished")
    }
  )
}
