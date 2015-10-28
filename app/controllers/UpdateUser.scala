package controllers

import javax.inject.Inject
import play.api.mvc.Controller
import services.AdminApi
import util.Logging

class UpdateUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def save = AuthAction {
    request => {
      Ok("200")
    }
  }

}
