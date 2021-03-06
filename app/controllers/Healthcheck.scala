package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import services.AdminApi

class HealthCheck @Inject() (adminApi: AdminApi) extends Controller {

  def healthCheck = Action {
    Ok("200 OK")
  }
}
