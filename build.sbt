import Dependencies._

name := "prime-grpc-scala-akka"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.iamsmkr"
ThisBuild / scalaVersion := "2.12.14"

lazy val root = (project in file("."))
  .aggregate(`prime-proxy`, `prime-generator`, `prime-common`)

lazy val `prime-common` = (project in file("prime-common"))
  .settings(
    libraryDependencies += typesafeConfig
  )

lazy val `prime-generator` = (project in file("prime-generator"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
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
      actorTestKitTyped,
      akkaTestKit,
      protobufSource
    )
  )
  .dependsOn(`prime-common`)

lazy val `prime-proxy` = (project in file("prime-proxy"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(
    dockerExposedPorts := Seq(8080),
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
      scalaTest,
      scalaMock,
      akkaTestKit,
      akkaHttpTestkit,
      akkaStreamTestkit,
      actorTestKitTyped,
      akkaDiscoveryKubernetes,
      logback,
      protobufSource,
      akkaHttpCors
    )
  )
  .dependsOn(`prime-common`)
