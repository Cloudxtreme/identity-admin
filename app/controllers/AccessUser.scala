package controllers

import javax.inject.Inject
import config.Config
import models.Forms._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play._
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

import scala.language.implicitConversions

class AccessUser @Inject() (adminApi: AdminApi, conf: Config) extends Controller with AuthActions with Logging {

  def getUser(searchQuery: String, userId: String) = AuthAction.async { implicit request =>
    adminApi.getFullUser(userId).map {
      case Right(user) =>
        val form = createForm(user)
        val error = request.flash.get("error")
        val formWithErrors = if(error.isDefined) form.withGlobalError(error.getOrElse("Unknown")) else form
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            formWithErrors,
            request.flash.get("message"),
            conf.baseProfileUrl + userId,
            conf.baseAvatarUrl + userId
          )
        )
      case Left(error) =>
        logger.error(s"Failed to find user. error: $error")
        Redirect(routes.Search.search()).flashing("error" -> error.toString)
      }
    }
}
