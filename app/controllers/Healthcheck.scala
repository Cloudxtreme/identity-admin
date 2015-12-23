package controllers

import play.api.mvc.{Action, Controller}

class HealthCheck extends Controller {

  def healthCheck = Action {
    Ok("200 OK")
  }
}
