name := "identity-admin"

scalaVersion := "2.11.7"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtNativePackager, UniversalPlugin, RiffRaffArtifact)

riffRaffPackageType := (packageZipTarball in Universal).value

riffRaffArtifactResources ++= Seq(
  baseDirectory.value / "provisioning" / "gu-identity-admin-dist"/"identity-admin-bootstrap.sh" ->
    "packages/identity-admin/identity-admin-bootstrap.sh",
  baseDirectory.value / "provisioning" / "gu-identity-admin-dist"/ "identity-admin-upstart.conf" ->
    "packages/identity-admin/identity-admin-upstart.conf"
)

riffRaffBuildIdentifier := "DEV"
riffRaffUploadArtifactBucket := "riffraff-artifact"
riffRaffUploadManifestBucket := "riffraff-builds"

play.PlayImport.PlayKeys.playDefaultPort := 8852
