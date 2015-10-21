name := "identity-admin"

scalaVersion := "2.11.7"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtNativePackager, UniversalPlugin, RiffRaffArtifact)

libraryDependencies ++= Seq(
  "com.gu" %% "play-googleauth" % "0.3.0",
  "org.scalatestplus" %% "play" % "1.4.0-M3",
  "org.webjars" % "bootstrap" % "3.3.5",
  ws,
  filters
)

riffRaffPackageType := (packageZipTarball in Universal).value

riffRaffBuildIdentifier := "DEV"
riffRaffUploadArtifactBucket := "riffraff-artifact"
riffRaffUploadManifestBucket := "riffraff-builds"

play.PlayImport.PlayKeys.playDefaultPort := 8852

addCommandAlias("devrun", "run -Dconfig.resource=dev.conf -Dlogs.home=logs")
