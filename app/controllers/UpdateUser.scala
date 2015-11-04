package controllers

import javax.inject.Inject
import models.Forms._
import models.{Forms, UserUpdateRequest}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import play.api.mvc.Result
import services.AdminApi
import play.api.libs.concurrent.Execution.Implicits._
import util.Logging
import scala.concurrent.Future

trait SaveAction extends Controller with Logging{
  val adminApi: AdminApi

  private[controllers] def doSave(userId: String, userRequest: UserUpdateRequest, searchQuery: String): Future[Result] = {
    adminApi.updateUserData(userId, userRequest).map {
      case Right(_) =>
        logger.info("Successfully updated user in database. Redirecting to search.")
        Redirect(routes.Search.search(searchQuery)).flashing("message" -> "User has been updated")
      case Left(error) =>
        logger.error(s"Failed to update user. error: $error")
        Redirect(routes.Search.search(searchQuery)).flashing("error" -> error.toString)
    }
  }
}

class UpdateUser @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with SaveAction {

  def save(searchQuery: String) = AuthAction.async { request =>
    userForm.bindFromRequest()(request).fold(
      errorForm => {
        val errorMessage = getFormErrorMessage(errorForm)
        Future(BadRequest(views.html.editUser(
          Messages("editUser.title"),
          Some(searchQuery),
          errorForm,
          None,
          Some(errorMessage)
        )))
      },
      userData => {
        val userRequest = userData.convertToUserUpdateRequest
        val userId = userData.id
        doSave(userId, userRequest, searchQuery)
      }
    )
  }

  private def getFormErrorMessage(form: Form[Forms.UserForm]): String = {
    form.error("username").map(error =>
      Messages("editUser.provideUsername")).getOrElse(Messages("editUser.invalidSubmission")
    )
  }
}
