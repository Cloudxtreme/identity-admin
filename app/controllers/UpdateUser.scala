package controllers

import javax.inject.Inject
import models.Forms._
import play.api.mvc.{Action, Controller}
import services.AdminApi
import util.Logging


class UpdateUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def save = Action {
    request => {
      userForm.bindFromRequest()(request).fold(
        formWithErrors => {
          println(formWithErrors)
          Ok("201")},
        userData => {
          println(userData)
          Ok("200")}
      )

    }
  }

}
