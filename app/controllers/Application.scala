package controllers

import play.api.Logger
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Logger.info("Index page hit.")
    Ok(views.html.index())
  }

  def healthCheck = Action {
    Ok("200 OK")
  }
}
