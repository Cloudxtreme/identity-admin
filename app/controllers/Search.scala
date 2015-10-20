package controllers

import models.SearchResponse
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

object Search extends Controller with AuthActions with Logging {

  def search(searchQuery: String) = AuthAction.async {

    AdminApi.getUsers(searchQuery).map{response =>
      val json = Json.parse(response.body)
      val searchResponse = SearchResponse.create(json)
      Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, searchResponse))
  }
  }


}
