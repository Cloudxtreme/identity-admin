package controllers

import play.api.mvc.{Action, Controller}

object HealthCheck extends Controller {

  def healthCheck = Action {
    Ok("200 OK")
  }
}
