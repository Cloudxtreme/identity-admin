package services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.gu.googleauth.{GoogleServiceAccount, GoogleGroupChecker}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

object GoogleGroups {

  lazy val serviceAccount = GoogleServiceAccount(
    credentials.getServiceAccountId,
    credentials.getServiceAccountPrivateKey,
    credentials.getServiceAccountUser)

  def isAuthorised(email: String): Future[Either[String, Set[String]]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    val future = checker.retrieveGroupsFor(email)
    future.map(Right(_)) recover {
      case _ => Left("Future Failed")
    }
  }

  private val credentials: GoogleCredential = {
    val fileInputStream = new FileInputStream("/etc/gu/identity-admin-cert.json")
    GoogleCredential.fromStream(fileInputStream)
  }

}
