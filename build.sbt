name := "identity-admin"

scalaVersion := "2.11.7"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtNativePackager, UniversalPlugin, RiffRaffArtifact)

libraryDependencies ++= Seq(
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.gu" %% "play-googleauth" % "0.3.2",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.5",
  "com.typesafe.akka" %% "akka-agent" % "2.4.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.10.30",
   ws,
  filters
)

mappings in Universal ++= (baseDirectory.value / "deploy" * "*" get) map (x => x -> ("deploy/" + x.getName))

riffRaffPackageType := (packageZipTarball in Universal).value

riffRaffBuildIdentifier := "DEV"
riffRaffUploadArtifactBucket := "riffraff-artifact"
riffRaffUploadManifestBucket := "riffraff-builds"

play.PlayImport.PlayKeys.playDefaultPort := 8852
routesGenerator := InjectedRoutesGenerator
routesImport += "auth.LoginError"

addCommandAlias("devrun", "run -Dconfig.resource=dev.conf -Dlogs.home=logs")