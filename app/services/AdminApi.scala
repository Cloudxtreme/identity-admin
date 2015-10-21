package services

import javax.inject.Singleton
import models.SearchResponse
import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

@Singleton
class AdminApi {
  private val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  private val searchUrl = baseUrl + "/user/search"

  def getUsers(searchQuery: String):Future[SearchResponse] = {
    WS.url(searchUrl).withQueryString("query" -> searchQuery).get.map{
      response => Json.parse(response.body).as[SearchResponse]
    }
  }

}
