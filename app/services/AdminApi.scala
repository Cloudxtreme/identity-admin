package services

import javax.inject.Singleton
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

@Singleton
class AdminApi extends Logging{

  def getUsers(searchQuery: String): Future[Either[CustomError, SearchResponse]] = {
    val searchUrl = getSearchUrl
    WS.url(searchUrl).withQueryString("query" -> searchQuery).get.map(
      response => checkResponse(response.status, response.body)
    ).recover { case e: Any =>
      {
        logger.error(e.getMessage)
        Left(CustomError("Fatal Error", "Contact identity team."))
      }}
  }

  def getSearchUrl = {
    val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
    baseUrl + "/user/search"
  }

  def checkResponse(status: Int, body: String): Either[CustomError, SearchResponse] =
    Try(
      if (status == 200) {
        Right(Json.parse(body).as[SearchResponse])
      } else {
        Left(Json.parse(body).as[CustomError])
      }
    ).getOrElse(Left(CustomError("Fatal Error", "Contact identity team.")))
}
