package controllers

import org.slf4j.LoggerFactory
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    val logger = LoggerFactory.getLogger(this.getClass)
    logger.info("Index page hit.")
    Ok(views.html.index())
  }

  def healthCheck = Action {
    Ok("200 OK")
  }
}
