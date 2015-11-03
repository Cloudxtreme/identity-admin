package services

import javax.inject.Inject
import models.SearchResponse
import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import util.Logging
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.Try

case class CustomError(message: String, details: String)

object CustomError {
  implicit val format = Json.format[CustomError]
}

class AdminApi @Inject() (requestSigner: RequestSigner) extends Logging{


  lazy val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  lazy val searchUrl = s"$baseUrl/user/search"
  def deleteUrl(id: String) = s"$baseUrl/user/$id"
  def sendValidationEmailUrl(id: String) = s"$baseUrl/user/$id/send-validation-email"

  def getUsers(searchQuery: String): Future[Either[CustomError, SearchResponse]] = {
    requestSigner.sign(WS.url(searchUrl).withQueryString("query" -> searchQuery)).get().map(
      response => checkResponse[SearchResponse](response.status, response.body, 200, x => Json.parse(x).as[SearchResponse])
    ).recover { case e: Any =>
        logger.error("Could not retrieve user via admin api",e.getMessage)
        Left(CustomError("Fatal Error", "Contact identity team."))
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
      Left(CustomError("Fatal Error", "Contact identity team."))
    }

  def delete(id: String): Future[Either[CustomError, Boolean]] = {
    requestSigner.sign(WS.url(deleteUrl(id))).delete().map(response =>
      checkResponse[Boolean](response.status, response.body, 204, x => true)
    ).recover { case e: Throwable =>
      logger.error("Could not delete user via admin api", e.getMessage)
      Left(CustomError("Fatal Error", "Contact identity team."))
    }
  }

  def sendEmailValidation(id: String): Future[Either[CustomError, Boolean]] = {
    requestSigner.sign(WS.url(sendValidationEmailUrl(id))).post("").map(response =>
      checkResponse[Boolean](response.status, response.body, 204, x => true)
    ).recover { case e: Throwable =>
      logger.error("Could not send email validation via admin api", e.getMessage)
      Left(CustomError("Fatal Error", "Contact identity team."))
    }
  }
}
