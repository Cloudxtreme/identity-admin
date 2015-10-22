package services

import javax.inject.Singleton
import models.SearchResponse
import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import scala.language.implicitConversions

case class CustomError(message: String, details: String)

object CustomError {
  implicit val format = Json.format[CustomError]
}

@Singleton
class AdminApi{
  private val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  private val searchUrl = baseUrl + "/user/search"

  def getUsers(searchQuery: String): Future[Either[CustomError, SearchResponse]] = {
    WS.url(searchUrl).withQueryString("query" -> searchQuery).get.map {
      response =>
        if (response.status == 200) {
          Right(Json.parse(response.body).as[SearchResponse])
        } else {
          Left(Json.parse(response.body).as[CustomError])
        }
      }
  }
}
