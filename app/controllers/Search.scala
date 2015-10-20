package controllers

import models.SearchResponse
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.{Flash, Controller}
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

object Search extends Controller with AuthActions with Logging {

  def search(searchQuery: String) = AuthAction.async {

    AdminApi.getUsers(searchQuery).map{response =>
      val searchResponse = Json.parse(response.body).as[SearchResponse]
      if (searchResponse.total < 1) {
        Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, searchResponse, Some(Messages("searchResults.errorMessage"))))
      } else {
        Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, searchResponse, None))
      }

  }
  }


}
