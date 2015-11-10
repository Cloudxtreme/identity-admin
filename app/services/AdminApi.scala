package services

import javax.inject.Inject
import models.{UserUpdateRequest, SearchResponse, User}
import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.ws.{WSResponse, WS}
import play.api.libs.concurrent.Execution.Implicits._
import util.Logging
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.Try

case class CustomError(message: String, details: String){
  override def toString: String ={
    s"$message : $details"
  }
}

object CustomError {
  implicit val format = Json.format[CustomError]
}

class AdminApi @Inject() (requestSigner: RequestSigner) extends Logging {

  lazy val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  lazy val baseRootUrl = current.configuration.getString("identity-admin.adminApi.baseRootUrl").get

  lazy val searchUrl = s"$baseUrl/user/search"

  lazy val authHealthCheckUrl = s"$baseRootUrl/authHealthcheck"

  def authHealthCheck: Future[Either[CustomError, String]] = {
    requestSigner.sign(WS.url(authHealthCheckUrl)).get().map ( response => response.status match {
      case 200 => Right("OK 200")
      case _  => Left(CustomError("API auth failed", s"API status code: ${response.status}"))
    }
    ).recover { case e: Throwable =>
        Left(CustomError("API connection failed", e.getMessage))
    }
  }

  def  accessUserUrl(id: String) = s"$baseUrl/user/$id"

  def sendValidationEmailUrl(id: String) = s"$baseUrl/user/$id/send-validation-email"
  lazy val errorEmail = current.configuration.getString("identity-admin.email.error").get
  val contact = "Contact identity team: " + errorEmail
  
  def getUsers(searchQuery: String): Future[Either[CustomError, SearchResponse]] = {
    requestSigner.sign(WS.url(searchUrl).withQueryString("query" -> searchQuery)).get().map(
      response => checkResponse[SearchResponse](response.status, response.body, 200, x => Json.parse(x).as[SearchResponse])
    ).recover { case e: Any =>
        logger.error("Future Failed: could not connect to API. {}",e.getMessage)
        Left(CustomError("Fatal Error", contact))
      }
  }

  def getFullUser(userId: String): Future[Either[CustomError, User]] = {
    requestSigner.sign(WS.url(accessUserUrl(userId))).get().map(
      response => checkResponse[User](response.status, response.body, 200, x => Json.parse(x).as[User])
    ).recover {
      case e: Any =>
        logger.error("Future Failed: could not connect to API",e.getMessage)
        Left(CustomError("Fatal Error", contact))
    }
  }

  def updateUserData(userId: String, userData: UserUpdateRequest): Future[Either[CustomError, User]] = {
    requestSigner.sign(WS.url(accessUserUrl(userId))).put(Json.toJson(userData)).map(
      response => checkResponse[User](response.status, response.body, 200, x => Json.parse(x).as[User])
    ).recover {
      case e: Any =>
        logger.error("Future Failed: could not connect to API",e.getMessage)
        Left(CustomError("Fatal Error", contact))
    }
  }

  def checkResponse[T](status: Int, body: String, successStatus: Int, successMapper: String => T): Either[CustomError, T] =
    Try(
      if (status == successStatus) {
        Right(successMapper(body))
      } else {
        Left(Json.parse(body).as[CustomError])
      }
    ).getOrElse{
      logger.error(s"Invalid response from API could not be parsed. Status: $status, Body: $body.")
      Left(CustomError("Fatal Error", contact))
    }

  def delete(id: String): Future[Either[CustomError, Boolean]] = {
    requestSigner.sign(WS.url(accessUserUrl(id))).delete().map(response =>
      checkResponse[Boolean](response.status, response.body, 204, x => true)
    ).recover { case e: Throwable =>
      logger.error("Could not delete user via admin api", e.getMessage)
      Left(CustomError("Fatal Error", contact))
    }
  }

  def sendEmailValidation(id: String): Future[Either[CustomError, Boolean]] = {
    requestSigner.sign(WS.url(sendValidationEmailUrl(id))).post("").map(response =>
      checkResponse[Boolean](response.status, response.body, 204, x => true)
    ).recover { case e: Throwable =>
      logger.error("Could not send email validation via admin api", e.getMessage)
      Left(CustomError("Fatal Error", contact))
    }
  }
}
