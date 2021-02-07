
ThisBuild / organization := "com.kabasoft"
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version      := "0.4.0-SNAPSHOT"

ThisBuild / scalafmtOnCompile := true

ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  //"-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfatal-warnings",
  "-deprecation"
  //"-Ypartial-unification"
)

val catsVersion = "2.1.1"
//val catsEffectVersion = "2.0.0" //"1.3.0"
val http4sVersion = "0.21.18"
val circeVersion = "0.13.0"
val circeGenericVersion = "0.13.0"
val circeConfigVersion = "0.7.0"
val doobieVersion = "0.8.6"
//val logbackVersion = "1.2.3"
val logbackVersion ="1.3.0-alpha5"
val EnumeratumCirceVersion = "1.6.1"
val mockitoScalaVersion = "1.4.0-beta.8"
val `zio-version`= "1.0.3" //"1.0-RC-5"
val `zio-interop` = "2.2.0.1"
val KindProjectorVersion = "0.11.0"
val LogbackVersion = "1.2.3"
val ScalaCheckVersion = "1.14.3"
val ScalaTestVersion = "3.2.0"
val ScalaTestPlusVersion = "3.2.0.0"
val FlywayVersion = "6.4.4"
val TsecVersion = "0.2.1"
val H2Version = "1.4.200"
val zioVersion = "1.0.3"



lazy val commonDependencies = Seq(
  "org.typelevel" %% "cats-core"   % catsVersion,
 // "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "ch.qos.logback" %  "logback-classic" % logbackVersion,
  "com.h2database" % "h2" % H2Version,
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
  "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % ScalaTestPlusVersion % Test,
"io.github.jmcardon" %% "tsec-common" % TsecVersion,
"io.github.jmcardon" %% "tsec-password" % TsecVersion,
"io.github.jmcardon" %% "tsec-mac" % TsecVersion,
"io.github.jmcardon" %% "tsec-signatures" % TsecVersion,
"io.github.jmcardon" %% "tsec-jwt-mac" % TsecVersion,
"io.github.jmcardon" %% "tsec-jwt-sig" % TsecVersion,
"io.github.jmcardon" %% "tsec-http4s" % TsecVersion,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test"          % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt"      % zioVersion % "test"
)

lazy val databaseDependencies = Seq(
  "org.tpolecat" %% "doobie-quill"             %  doobieVersion,
  "org.tpolecat" %% "doobie-core"              % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"            % doobieVersion,
  "org.tpolecat" %% "doobie-postgres"          % doobieVersion,
  "org.flywaydb" % "flyway-core" % FlywayVersion,
  "com.beachape" %% "enumeratum-circe" % EnumeratumCirceVersion,
  "org.tpolecat" %% "doobie-scalatest"         % doobieVersion % Test
)

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-generic"        % circeVersion,
  "io.circe" %% "circe-literal"        % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeGenericVersion,
  "io.circe" %% "circe-parser"         % circeVersion,
  "io.circe" %% "circe-config"         % circeConfigVersion
)

addCompilerPlugin("org.typelevel" % "kind-projector" % KindProjectorVersion cross CrossVersion.full)


lazy val iws = project
  .in(file("."))
  .aggregate(domain, application, infrastructure, userAPI)
  .dependsOn(domain, application, infrastructure, userAPI)

lazy val domain = project
  .in(file("domain"))
  .settings(moduleName := "domain", name := "Domain")
  .settings(libraryDependencies := commonDependencies)

lazy val application = project
  .in(file("application"))
  .settings(moduleName := "application", name := "Application")
  .settings(libraryDependencies := commonDependencies)
  .dependsOn(domain)

lazy val infrastructure = project
  .in(file("infrastructure"))
  .settings(moduleName := "infrastructure", name := "Infrastructure")
  .settings(libraryDependencies := commonDependencies ++ databaseDependencies ++ circeDependencies)
  .dependsOn(domain, application)
   addCompilerPlugin(
   ("org.typelevel" %% "kind-projector" % KindProjectorVersion).cross(CrossVersion.full),
  )
enablePlugins(ScalafmtPlugin, JavaAppPackaging, GhpagesPlugin, MicrositesPlugin, TutPlugin)

lazy val userAPI = project
  .in(file("user-api"))
  .settings(moduleName := "user-api", name := "User API")
  .settings(libraryDependencies := commonDependencies ++ circeDependencies ++ Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion % Test,
     "dev.zio" %% "zio" %  `zio-version`,
     "dev.zio" %% "zio-test" % `zio-version` % "test",
     "dev.zio" %% "zio-test-sbt" % `zio-version` % "test",
     //"dev.zio" %% "zio-interop-shared" % `zio-version`,
     "dev.zio" %% "zio-interop-cats" % `zio-interop`
  ))
  .settings(Seq(
    fork in run := true,
    cancelable in Global := true
  ))
  .dependsOn(domain, application, infrastructure)
addCompilerPlugin("org.typelevel" % "kind-projector" % KindProjectorVersion cross CrossVersion.full)

