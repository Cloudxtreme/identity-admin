package services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{GoogleServiceAccount, GoogleGroupChecker}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Logging

import scala.concurrent.Future
import scala.util.Try

object GoogleGroups extends Logging {
  import Authorisation._

  val requiredGroups = Set(TWO_FACTOR_AUTH_GROUP, USER_ADMIN_GROUP)

  private val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    GoogleAuthConf.impersonatedUser)

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
      case Right(groups) => isAuthorised(required = requiredGroups, groups = groups)
      case Left(_) => {
        logger.info("{} is not in correct groups", email)
        false
      }
    })
  }
}

object Authorisation {
  val TWO_FACTOR_AUTH_GROUP = "2fa_enforce@guardian.co.uk"
  val USER_ADMIN_GROUP = "identity.userhelp@guardian.co.uk"

  def isAuthorised(required: Set[String], groups: Set[String]): Boolean = (required & groups) == required
}
