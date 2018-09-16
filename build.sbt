lazy val root = (project in file("."))
  .settings(
    name := "forecastx",
    organization := "pt.necosta",
    scalaVersion := "2.12.6",
    version := "0.0.1"
  )

val testDependencies = Seq("org.scalatest" %% "scalatest" % "3.0.5" % "test")

libraryDependencies ++= testDependencies

// Format options
scalafmtOnCompile in ThisBuild := true
scalafmtTestOnCompile in ThisBuild := true
scalafmtFailTest in ThisBuild := true
