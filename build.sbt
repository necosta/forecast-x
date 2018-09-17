lazy val root = (project in file("."))
  .settings(
    name := "forecastx",
    organization := "pt.necosta",
    scalaVersion := "2.11.12"
  )

val sparkVersion = "2.3.1"

val coreDependencies = Seq("org.apache.spark" %% "spark-sql" % sparkVersion,
                           "org.apache.spark" %% "spark-mllib" % sparkVersion)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.holdenkarau" %% "spark-testing-base" % (sparkVersion + "_0.10.0") % "test")

libraryDependencies ++= coreDependencies ++ testDependencies

// Format options
scalafmtOnCompile in ThisBuild := true
scalafmtTestOnCompile in ThisBuild := true
scalafmtFailTest in ThisBuild := true

// Disable parallel execution
parallelExecution in ThisBuild := false
parallelExecution in Test := false

// From: https://medium.com/@mrpowers/how-to-cut-the-run-time-of-a-spark-sbt-test-suite-by-40-52d71219773f
fork in Test := true
javaOptions in Test ++= Seq("-Xms512M",
                            "-Xmx2048M",
                            "-XX:+CMSClassUnloadingEnabled")

coverageEnabled in Test := true
