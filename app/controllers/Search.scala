package controllers

import controllers.Application._
import models.SearchResult
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.{Session, RequestHeader, Controller}
import util.Logging

/**
 * Created by scoleman on 16/10/15.
 */
object Search extends Controller with AuthActions with Logging{

  def search(searchQuery: String) = AuthAction {
    Ok(views.html.searchResults(Messages("searchResults.title"),searchQuery, SearchResult.mockData()))
  }


}
