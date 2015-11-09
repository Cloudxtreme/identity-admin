package controllers

import javax.inject.Inject
import models.SearchResponse
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

class Search @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def search(searchQuery: String) = AuthAction.async { request =>
    adminApi.getUsers(searchQuery) map {
      case Right(searchResult) =>
        Ok(
            views.html.searchResults(
              Messages("searchResults.title"),
              Some(searchQuery),
              searchResult,
              request.flash.get("message"),
              request.flash.get("error")
          )
        )
      case Left(error) =>
        Ok(
          views.html.searchResults(
            Messages("searchResults.title"),
            Some(searchQuery),
            SearchResponse(0, false),
            request.flash.get("message"),
            Some(error.toString)
          )
        )
    }
  }
}
