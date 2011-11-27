import sbt._
import Keys._
import AndroidKeys._

object BuildSettings {
  val settings = Seq(
    name := "stools",
    organization := "net.xzlong.android",
    version := "0.2-SNAPSHOT",
    scalaVersion := "2.8.2",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    platformName in Android := "android-10",
	  useProguard in Android := true,
    proguardInJars in Android <+= scalaInstance.map(_.libraryJar),
    proguardOption in Android := "-verbose"

  )

  val buildSettings = Defaults.defaultSettings ++ AndroidProject.androidSettings ++ settings
}

object Resolvers {

}

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "1.5.1" % "test"
}

object AndroidBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  lazy val memoryProject = Project(
    "stools",
    file("."),
    settings = buildSettings ++ Seq(libraryDependencies := Seq(scalaTest))
  )
}