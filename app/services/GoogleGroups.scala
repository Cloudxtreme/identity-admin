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

  def isAuthorised(email: String): Future[Either[String, Set[String]]] = {
    val checker = new GoogleGroupChecker(serviceAccount)
    val f = checker.retrieveGroupsFor(email)
    f.map(Right(_)) recover {
      case _ => Left("Future failed")
    }
  }
}
