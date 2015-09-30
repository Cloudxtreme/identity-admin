package controllers

import play.api.mvc._
import util.Logging

object Application extends Controller with Logging{

  def index = Action {
    logger.info("Index page hit.")
    Ok(views.html.index())
  }

  def healthCheck = Action {
    Ok("200 OK")
  }
}
