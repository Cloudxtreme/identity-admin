package services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{GoogleServiceAccount, GoogleGroupChecker}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.Try

object GoogleGroups {

  lazy val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    credentials.getServiceAccountUser)

  private val credentials: GoogleCredential = {
    val fileInputStream = Try(new FileInputStream("/etc/gu/identity-admin-cert.json"))
    GoogleCredential.fromStream(fileInputStream.get)
  }

  val requiredGroups = Set("2FA_enforce","useradmin")

  def userGroups(email: String): Future[Either[String, Set[String]]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    val f = checker.retrieveGroupsFor(email)
    f.map(Right(_)) recover {
      case e => Left("Future failed" + e.getMessage)
    }
  }

  def isAuthorised(required: Set[String], groups: Set[String]): Boolean = (required & groups) == required

  def isUserAdmin(email: String): Future[Boolean] = {
    userGroups(email).map( _ match {
      case Right(groups) => isAuthorised(requiredGroups, groups)
      case Left(_) => false
    })
  }
}
