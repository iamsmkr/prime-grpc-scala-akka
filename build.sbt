import Dependencies._

name := "prime-grpc-scala-akka"

lazy val root = (project in file("."))
  .aggregate(`prime-proxy`, `prime-generator`)

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
