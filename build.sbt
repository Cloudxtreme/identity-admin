name := "identity-admin"

scalaVersion := "2.11.7"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtNativePackager, UniversalPlugin, RiffRaffArtifact)

libraryDependencies ++= Seq(
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.gu" %% "play-googleauth" % "0.3.2",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.2" % "test",
  ws,
  filters
)

riffRaffPackageType := (packageZipTarball in Universal).value

riffRaffBuildIdentifier := "DEV"
riffRaffUploadArtifactBucket := "riffraff-artifact"
riffRaffUploadManifestBucket := "riffraff-builds"

play.PlayImport.PlayKeys.playDefaultPort := 8852
routesGenerator := InjectedRoutesGenerator

addCommandAlias("devrun", "run -Dconfig.resource=dev.conf -Dlogs.home=logs")

javaOptions in Test += "-Dconfig.file=conf/test.conf"
addCommandAlias("test", "test-small")
addCommandAlias("test-small", "testOnly -- -l Large")
addCommandAlias("test-large", "testOnly -- -n Large")
addCommandAlias("test-all", "testOnly --")
