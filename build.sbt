ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "my-stock"


val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"

lazy val root = (project in file("."))
.settings(
  name := "root",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  )
)
