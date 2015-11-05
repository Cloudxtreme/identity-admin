name := """functional-tests"""

version := "1.0"

scalaVersion := "2.11.7"

resolvers := Seq(Classpaths.typesafeReleases,
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.2" % "test")