package controllers

import play.api.mvc.{Action, Controller}

object Healthcheck extends Controller {

  def healthCheck = Action {
    Ok("200 OK")
  }
}
