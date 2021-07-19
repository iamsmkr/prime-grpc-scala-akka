import Dependencies._

name := "prime-grpc-scala-akka"
scalaVersion := "2.13.3"

lazy val root = (project in file("."))
  .aggregate(`prime-proxy`, `prime-generator`)

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
      logback
    )
  )

lazy val `prime-proxy` = (project in file("prime-proxy"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
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
      logback
    ),
    dockerExposedPorts := Seq(8080),
  )
