scalaVersion := "2.13.1"
organization := "my-stock"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"
val GoogleJuiceVersion = "4.1.0"
val QuandlVersion = "2.0.0"
val CatsVersion = "2.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "root",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.google.inject" % "guice" % GoogleJuiceVersion,
      "com.jimmoores" % "quandl-core" % QuandlVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-free" % CatsVersion,
      compilerPlugin(
        "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
      )
    ),
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Ywarn-dead-code",
      "-Ymacro-annotations"
    )
  )
