package services

import java.io.FileInputStream

import auth.{GroupsValidationFailed, LoginError}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{UserIdentity, GoogleServiceAccount, GoogleGroupChecker}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Logging

import scala.concurrent.Future
import scala.util.Try

object GoogleGroups extends Logging {
  import Authorisation._

  val requiredGroups = Set(TWO_FACTOR_AUTH_GROUP, GoogleAuthConf.userAdminGroup)

  private val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    GoogleAuthConf.impersonatedUser)

  private lazy val credentials: GoogleCredential = {
    val fileInputStream = Try(new FileInputStream("/etc/gu/identity-admin-cert.json"))
    GoogleCredential.fromStream(fileInputStream.get)
  }

  def userGroups(email: String): Future[Either[LoginError, Set[String]]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    val f = checker.retrieveGroupsFor(email)
    f.map(Right(_)) recover {
      case e => Left(GroupsValidationFailed())
    }
  }

  def isUserAdmin(identity: UserIdentity): Future[Either[LoginError, UserIdentity]] = {
    userGroups(identity.email).map {
      case Right(groups) if isAuthorised(required = requiredGroups, groups = groups) => Right(identity)
      case Left(error) =>
        logger.info("{} is not in correct groups", identity.email)
        Left(GroupsValidationFailed())
    }
  }
}

object Authorisation {
  val TWO_FACTOR_AUTH_GROUP = "2fa_enforce@guardian.co.uk"
  def isAuthorised(required: Set[String], groups: Set[String]): Boolean = (required & groups) == required
}
