import play.sbt.PlayScala
import sbt.Keys._
import sbt._

object IdentityAdmin extends Build {

  lazy val root = Project(id = "identity-admin",
                          base = file(".")) aggregate(functionalTests)

  lazy val functionalTests =
    Project(id = "functional-tests", base = file("functional-tests"))

}
