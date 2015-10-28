package controllers

import javax.inject.Inject
import models.Forms.UserData
import models.{Forms, User}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.Controller
import services.{CustomError, AdminApi}
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data.Forms._

import scala.language.implicitConversions

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  val blankUser = User("", "")

  def getUser(searchQuery: String, userId: String) = AuthAction.async { request =>
    adminApi.getFullUser(userId).map {
      case Right(user) =>
        val form = createForm(user)
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery), user,form, request.flash.get("error")))
      case Left(error) =>
        val blankForm = createForm(blankUser)
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery), blankUser,blankForm, Some(error.toString)))
      }
    }

  def createForm(user: User): Form[UserData] = {
    Forms.userForm.fill(UserData(
      user.id,
      user.email,
      user.personalDetails.firstName.getOrElse(""),
      user.personalDetails.lastName.getOrElse(""),
      user.username.getOrElse(""),
      user.status.receiveGnmMarketing.getOrElse(false),
      user.status.receive3rdPartyMarketing.getOrElse(false))
    )
  }
}
