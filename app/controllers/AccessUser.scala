package controllers

import javax.inject.Inject
import models.Forms._
import models.{Forms, User}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

import scala.language.implicitConversions

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  val blankUser = User("", "")

  def getUser(searchQuery: String, userId: String) = AuthAction.async { request =>
    adminApi.getFullUser(userId).map {
      case Right(user) =>
        val form = createForm(user)
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            Some(searchQuery),
            user,
            form,
            request.flash.get("error")
          )
        )
      case Left(error) =>
        val blankForm = createForm(blankUser)
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            Some(searchQuery),
            blankUser,
            blankForm,
            Some(error.toString)
          )
        )
      }
    }

  def createForm(user: User): Form[UserForm] = {
    Forms.userForm.fill(UserForm(
      user.email,
      user.username.getOrElse(""),
      Some(user.personalDetails.firstName.getOrElse("")),
      Some(user.personalDetails.lastName.getOrElse("")),
      Some(user.status.receiveGnmMarketing.getOrElse(false)),
      Some(user.status.receive3rdPartyMarketing.getOrElse(false)))
    )
  }
}
