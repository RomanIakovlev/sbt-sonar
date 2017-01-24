import java.security.Permission

import sbt._
import sbt.plugins.JvmPlugin

object Misc extends AutoPlugin {

  println(
    "Security manager is required, because SonarRunner " +
      "calls System.exit internally, " +
      "making sbt-scripted tests fail")

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = JvmPlugin

  class SonarSecurityManager extends SecurityManager {
    override def checkExit(status: Int): Unit = throw new SecurityException

    override def checkPermission(perm: Permission): Unit = ()

    override def checkPermission(perm: Permission, context: scala.Any): Unit = ()
  }

  println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")

  val sm = new SonarSecurityManager
  System.setSecurityManager(sm)

}
