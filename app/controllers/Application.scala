package controllers

import models.SearchResult
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc._
import util.Logging

object Application extends Controller with AuthActions with Logging {

  def index = AuthAction {
    logger.info("Index page hit.")
    Ok(views.html.index(Messages("index.title"), Messages("index.searchBarText")))
  }

  def search(searchQuery: String) = Action {
    Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, SearchResult.mockData()))
  }
}
