import Dependencies._

name := "prime-grpc-scala-akka"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.iamsmkr"

lazy val root = (project in file("."))
  .aggregate(`prime-common`, `prime-proxy`, `prime-generator`)

lazy val `prime-common` = (project in file("prime-common"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalaVersion := "2.12.14",
    crossPaths := false
  )

lazy val `prime-generator` = (project in file("prime-generator"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(scalaVersion := "2.12.14")
  .settings(
    dockerExposedPorts := Seq(8080),
    libraryDependencies ++= Seq(
      akka,
      akkaTyped,
      akkaSlf4j,
      akkaStream,
      akkaDiscovery,
      akkaHttp,
      akkaHttp2Support,
      logback,
      scalaTest,
      scalaCheck,
      akkaTestKit,
      protobufSource
    )
  )
  .dependsOn(`prime-common`)

lazy val `prime-proxy` = (project in file("prime-proxy"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(scalaVersion := "2.13.3")
  .settings(
    libraryDependencies ++= Seq(
      akkaTyped,
      akkaSlf4j,
      akkaDiscovery,
      akkaStream,
      akkaParsing,
      akkaHttpCore,
      akkaHttp,
      akkahttpSprayJson,
      akkaHttp2Support,
      akkaDiscoveryKubernetes,
      logback,
      protobufSource
    ),
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(`prime-common`)
