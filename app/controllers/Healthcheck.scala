package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

class HealthCheck @Inject() extends Controller {

  def healthCheck = Action {
    Ok("200 OK")
  }
}
