import sbt._

object Dependencies {

  private lazy val akkaVersion = "2.6.14"
  private lazy val discoveryVersion = "1.0.9"
  private lazy val akkaHttpVersion = "10.2.3"
  private lazy val akkaHttpCorsVersion = "1.1.1"
  private lazy val logbackVersion = "1.2.3"
  private lazy val typeSafeConfigVersion = "1.3.1"
  private lazy val protobufSourceVersion = "0.1"
  private lazy val scalaTestVersion = "3.2.9"
  private lazy val scalaCheckVersion = "3.2.9.0"
  private lazy val scalaMockVersion = "5.1.0"

  lazy val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  lazy val akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % akkaVersion
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  lazy val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
  lazy val actorTestKitTyped = "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test

  lazy val akkaParsing = "com.typesafe.akka" %% "akka-parsing" % akkaHttpVersion
  lazy val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkahttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaHttp2Support = "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  lazy val akkaHttpCors = "ch.megard" %% "akka-http-cors" % akkaHttpCorsVersion

  lazy val akkaDiscoveryKubernetes = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % discoveryVersion

  lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion

  lazy val typesafeConfig = "com.typesafe" % "config" % typeSafeConfigVersion

  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "it,test"
  lazy val scalaCheck = "org.scalatestplus" %% "scalacheck-1-15" % scalaCheckVersion % Test
  lazy val scalaMock = "org.scalamock" %% "scalamock" % scalaMockVersion % Test

  lazy val protobufSource = "com.iamsmkr" %% "prime-protobuf" % protobufSourceVersion % "protobuf-src"
}
