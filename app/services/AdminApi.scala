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

import models.User

case class CustomError(message: String, details: String)

object CustomError {
  implicit val format = Json.format[CustomError]
}

class AdminApi @Inject() (requestSigner: RequestSigner) extends Logging{

  lazy val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  lazy val searchUrl = s"$baseUrl/user/search"
  def deleteUrl(id: String) = s"$baseUrl/user/$id"
  def getFullUserUrl(id: String) = s"$baseUrl/user/$id"

  def getUsers(searchQuery: String): Future[Either[CustomError, SearchResponse]] = {
    requestSigner.sign(WS.url(searchUrl).withQueryString("query" -> searchQuery)).get().map(
      response => checkResponse[SearchResponse](response.status, response.body, 200, x => Json.parse(x).as[SearchResponse])
    ).recover { case e: Any =>
        logger.error("Future Failed: could not connect to API",e.getMessage)
        Left(CustomError("Fatal Error", "Contact identity team."))
      }
  }

  def getFullUser(userId: String): Future[User] = {
    requestSigner.sign(WS.url(getFullUserUrl(userId))).get().map {
      response =>
        Json.parse(response.body).as[User]
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
      logger.error("Future Failed: could not connect to API", e.getMessage)
      Left(CustomError("Fatal Error", "Contact identity team."))
    }
  }
}
