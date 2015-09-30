name := "identity-admin"

scalaVersion := "2.11.7"

version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.gu" %% "play-googleauth" % "0.3.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  ws
)

enablePlugins(PlayScala, SbtNativePackager, UniversalPlugin, RiffRaffArtifact)

riffRaffPackageType := (packageZipTarball in Universal).value

riffRaffBuildIdentifier := "DEV"
riffRaffUploadArtifactBucket := "riffraff-artifact"
riffRaffUploadManifestBucket := "riffraff-builds"

play.PlayImport.PlayKeys.playDefaultPort := 8852
