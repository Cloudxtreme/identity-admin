package services

import javax.inject.Singleton
import models.SearchResponse
import play.api.Play._
import play.api.libs.json.{JsResultException, Json}
import play.api.libs.ws.{WSResponse, WS}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.Try

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
      response => AdminApi.checkResponse(response.status, response.body)
    }
  }
}

object AdminApi {
  def checkResponse(status: Int, body: String): Either[CustomError, SearchResponse] =
    Try(
      if (status == 200) {
        Right(Json.parse(body).as[SearchResponse])
      } else {
        Left(Json.parse(body).as[CustomError])
      }
    ).getOrElse(Left(CustomError("Fatal Error", "Contact identity team.")))
}
