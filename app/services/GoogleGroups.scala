package services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

import scala.concurrent.Future

object GoogleGroups {

  def isAuthorised(email: String): Future[Either[String, String]] = {
    Future.failed(new Exception)
  }

  def loadCredential: GoogleCredential = {
    val fileInputStream = new FileInputStream("/etc/gu/identity-admin-cert.json")
    GoogleCredential.fromStream(fileInputStream)
  }

}
