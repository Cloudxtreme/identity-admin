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
  val publicProfileUrl: String
  val avatarUrl: String

  private[controllers] def doSave(searchQuery: String, form: Form[Forms.UserForm]): Future[Result] = {
    form.fold(
      errorForm => {
        Future(BadRequest(views.html.editUser(
          Messages("editUser.title"),
          Some(searchQuery),
          errorForm,
          None,
          publicProfileUrl,
          avatarUrl
        )))
      },
      userData => {
        val userRequest = userData.convertToUserUpdateRequest
        val userId = userData.id
        update(userId, searchQuery, userRequest, form)
      }
    )
  }

  private[controllers] def update(userId: String,
                     searchQuery: String,
                     userRequest: UserUpdateRequest,
                     form: Form[Forms.UserForm]): Future[Result] = {

    adminApi.updateUserData(userId, userRequest).map {
      case Right(_) =>
        logger.info("Successfully updated user in database. Redirecting to search.")
        Redirect(routes.Search.search(searchQuery)).flashing("message" -> "User has been updated")
      case Left(error) =>
        logger.error(s"Failed to update user. error: $error")
        Ok(
          views.html.editUser(
            Messages("editUser.title"),
            Some(searchQuery),
            form.withGlobalError(error.toString),
            None,
            publicProfileUrl,
            avatarUrl
          )
        )
    }
  }
}

class UpdateUser @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with SaveAction {

  val publicProfileUrl: String = current.configuration.getString("identity-admin.editUser.baseProfileUrl").get
  val avatarUrl: String = current.configuration.getString("identity-admin.avatar.baseUrl").get

  def save(searchQuery: String) = AuthAction.async { request =>
    val form = userForm.bindFromRequest()(request)
    doSave(searchQuery, form)
  }
}
