package controllers

import javax.inject.Inject

import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import services.AdminApi
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import util.Logging

class Delete @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def delete(searchQuery: String, userId: String) = AuthAction.async {
    logger.info(s"Deleting user with id: $userId")
    adminApi.delete(userId).map {
      case Right(result) =>
        logger.info("Successfully deleted user. Redirecting to search.")
        Redirect(routes.Search.search(searchQuery)).flashing("message" -> Messages("deleteUser.success", userId))
      case Left(error) =>
        logger.error(s"Failed to delete user. error: $error")
        Redirect(routes.Application.getEditUserPage(searchQuery, userId)).flashing("error" -> error.message)
    }
  }
}
