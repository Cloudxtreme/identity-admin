package services
import java.io.FileInputStream

import auth.{GroupsValidationFailed, LoginError}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{UserIdentity, GoogleServiceAccount, GoogleGroupChecker}
import config.Config
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Logging

import scala.concurrent.Future
import scala.util.Try

import scalaz.{\/-, -\/, \/}

object GoogleGroups extends Logging {
  import Authorisation._

  val requiredGroups = Set(TWO_FACTOR_AUTH_GROUP, GoogleAuthConf.userAdminGroup)

  val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    GoogleAuthConf.impersonatedUser)

  private lazy val credentials: GoogleCredential = {
    val fileInputStream = Try(new FileInputStream("/etc/gu/identity-admin-cert.json"))
    GoogleCredential.fromStream(fileInputStream.get)
  }

  def userGroups(identity: UserIdentity): Future[Set[String]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    checker.retrieveGroupsFor(identity.email)
  }


  def isUserAdmin[A](identity: UserIdentity): Future[\/[LoginError, UserIdentity]] =
    Admin.isUserInAdminGroups(userGroups(_), identity, requiredGroups)
}

object Authorisation {
  val TWO_FACTOR_AUTH_GROUP = "2fa_enforce@guardian.co.uk"

  def isAuthorised(required: Set[String], groups: Set[String]): Boolean = (required & groups) == required
}

object Admin extends Logging {

  def isUserInAdminGroups(f: UserIdentity => Future[Set[String]], identity: UserIdentity, requiredGroups: Set[String]): Future[\/[LoginError, UserIdentity]] = {
    f(identity).map { groups =>
      if (Authorisation.isAuthorised(required = requiredGroups, groups = groups))  \/-(identity)
      else {
        logger.info("{} is not in correct groups", identity.email)
        -\/(GroupsValidationFailed())
      }
    }.recover {
      case e => -\/(GroupsValidationFailed())
    }
  }
}
