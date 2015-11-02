package controllers

import play.api.mvc.Action
import javax.inject.Inject
import models.Forms._
import models.User
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

class UpdateUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  val blankUser = User("","")

  def save = Action.async { request =>
    val userId = "10000001"
    val searchQuery = request.body.asFormUrlEncoded.get("searchQuery").head
    userForm.bindFromRequest()(request).fold(
      formWithErrors => {
        adminApi.getFullUser(userId).map {
          case Right(user) =>
            BadRequest(
              views.html.editUser(
                Messages("editUser.title"),
                Some(searchQuery),
                user,
                formWithErrors,
                Some(Messages("editUser.invalidSubmission"))
              )
            )
          case Left(error) =>
            BadRequest(
              views.html.editUser(
                Messages("editUser.title"),
                Some(searchQuery),
                blankUser,
                formWithErrors,
                Some(error.toString)
              )
            )
        }
      },
      userData => {
        val userRequest = userData.convertToUserUpdateRequest
        val userId = userData.id
        adminApi.updateUserData(userId, userRequest).map {
          case Right(_) => Redirect(routes.Search.search(searchQuery)).flashing("success" -> "User has been updated")
          case Left(error) => Redirect(routes.Search.search(searchQuery)).flashing("error" -> error.toString)
        }
      }
    )
  }
}
