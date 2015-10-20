package controllers

import models.{UserSummary, SearchResponse, SearchResult}
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.{JsArray, Reads, Json}
import play.api.mvc.{Session, RequestHeader, Controller}
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._
import models.CustomFormats._

object Search extends Controller with AuthActions with Logging {

  def search(searchQuery: String) = AuthAction.async {

    AdminApi.getUsers(searchQuery).map{response =>
      val json = Json.parse(response.body)
      val searchResponse = SearchResponse.create(json)
      Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery,SearchResult.mockData(), searchResponse.toString))
  }
  }


}
