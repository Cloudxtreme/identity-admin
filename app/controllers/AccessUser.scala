package controllers

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

import scala.language.implicitConversions

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def getUser(searchQuery: String, userId: String) = AuthAction.async { request =>
    adminApi.getFullUser("10000001").map {
      user =>
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery),
          user, request.flash.get("error")))
    }
  }
}