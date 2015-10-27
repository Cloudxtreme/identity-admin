package controllers

import javax.inject.Inject
import models.User
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.Controller
import services.{CustomError, AdminApi}
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

import scala.language.implicitConversions

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  val blankUser = User("", "")

  def getUser(searchQuery: String, userId: String) = AuthAction.async { request =>
    adminApi.getFullUser(userId).map {
      case Right(user) =>
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery), user, request.flash.get("error")))
      case Left(error) =>
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery), blankUser, Some(error.toString)))
      }
    }
  }
