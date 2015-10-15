package controllers

import models._
import play.api.mvc._
import util.Logging

object Application extends Controller with AuthActions with Logging {

  def index = AuthAction {
    logger.info("Index page hit.")
    Ok(views.html.index())
  }

  def search(searchQuery: String) = Action {
    Ok(views.html.searchResults(searchQuery, SearchResult.mockData()))
  }

  def getEditUserPage(searchQuery: String, userId: String) = Action {
    Ok(views.html.editUser(searchQuery, User.mockData()))
  }
}
