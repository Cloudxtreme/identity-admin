package controllers

import play.api.mvc._

object Application extends Controller with AuthActions {

  def index = AuthAction {
    Ok(views.html.index())
  }

  def healthCheck = Action {
    Ok("200 OK")
  }
}
