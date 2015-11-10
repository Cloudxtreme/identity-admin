package controllers

import javax.inject.Inject
import models.Forms._
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
    adminApi.getFullUser(userId).map {
      case Right(user) =>
        println(user)
        val form = createForm(user)
        val error = request.flash.get("error")
        val formWithErrors = if(error.isDefined) form.withGlobalError(error.getOrElse("Unknown")) else form
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            Some(searchQuery),
            formWithErrors,
            request.flash.get("message")
          )
        )
      case Left(error) =>
        logger.error(s"Failed to find user. error: $error")
        Redirect(routes.Search.search(searchQuery)).flashing("error" -> error.toString)
      }
    }
}
