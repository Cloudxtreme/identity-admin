package services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{GoogleServiceAccount, GoogleGroupChecker}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Logging

import scala.concurrent.Future
import scala.util.Try

object GoogleGroups extends Logging {

  val requiredGroups = Set("2FA_enforce","useradmin")

  private val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    credentials.getServiceAccountUser)

  private lazy val credentials: GoogleCredential = {
    val fileInputStream = Try(new FileInputStream("/etc/gu/identity-admin-cert.json"))
    GoogleCredential.fromStream(fileInputStream.get)
  }

  def userGroups(email: String): Future[Either[String, Set[String]]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    val f = checker.retrieveGroupsFor(email)
    f.map(Right(_)) recover {
      case e => Left("Future failed" + e.getMessage)
    }
  }

  def isUserAdmin(email: String): Future[Boolean] = {
    userGroups(email).map( _ match {
      case Right(groups) => Authorisation.isAuthorised(required = requiredGroups, groups = groups)
      case Left(_) => {
        logger.info("{} is not in correct groups", email)
        false
      }
    })
  }
}

object Authorisation {
  def isAuthorised(required: Set[String], groups: Set[String]): Boolean = (required & groups) == required
}
