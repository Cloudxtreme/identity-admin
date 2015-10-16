package controllers

import models.SearchResult
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.{Session, RequestHeader, Controller}
import util.Logging

object Search extends Controller with AuthActions with Logging{

  def search(searchQuery: String) = AuthAction {

    // take searchquery pass it into our Business Logic

    // Pass result of search into view

    Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, SearchResult.mockData()))
  }


}
