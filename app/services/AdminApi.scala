package services

import play.api.Play._
import play.api.libs.ws.WS

object AdminApi {
  private val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl").get
  private val searchUrl = baseUrl + "/user/search"

  def getUsers(searchQuery: String) = {
    WS.url(searchUrl).withQueryString("query" -> searchQuery).get
  }

}
