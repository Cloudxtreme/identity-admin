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
        val form = createForm(user)
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            Some(searchQuery),
            form,
            request.flash.get("message"),
            request.flash.get("error")
          )
        )
      case Left(error) =>
        logger.error(s"Failed to find user. error: $error")
        Redirect(routes.Search.search(searchQuery)).flashing("error" -> error.toString)
      }
    }
}
