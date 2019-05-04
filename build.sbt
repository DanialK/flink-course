name := "flink-course"
organization in ThisBuild := "com.solvemprobler"
scalaVersion in ThisBuild := "2.12.8"

lazy val dependencies = new {
  val scalaTic = "org.scalactic" %% "scalactic" % "3.0.5"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test

  val flinkVersion = "1.8.0"
  val flinkScala = "org.apache.flink" %% "flink-scala" % flinkVersion % Provided
  val flinkStreamingScala = "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % Provided

  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.26"
  val slf4jLog4j = "org.slf4j" % "slf4j-log4j12" % "1.7.26"
}

lazy val commonDependencies = Seq(
  dependencies.scalaTic,
  dependencies.scalaTest,
  dependencies.flinkScala,
  dependencies.flinkStreamingScala,
  dependencies.slf4jApi,
  dependencies.slf4jLog4j
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

lazy val settings = Seq(
  scalacOptions ++=  Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    wordCount
  )


lazy val wordCount = project
  .in(file("./word-count"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
