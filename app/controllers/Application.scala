package controllers

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc._
import util.Logging

class Application @Inject() extends Controller with AuthActions with Logging {

  def index = AuthAction {
    logger.info("Index page hit.")
    Ok(views.html.index(Messages("index.title"), None))
  }
}
