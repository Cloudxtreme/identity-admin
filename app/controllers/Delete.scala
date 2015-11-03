package controllers

import javax.inject.Inject

import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Result, Controller}
import services.AdminApi
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import util.Logging

import scala.concurrent.Future

trait DeleteAction extends Controller with Logging{
  val adminApi: AdminApi

  private[controllers] def doDelete(searchQuery: String, userId: String): Future[Result] = {
    logger.info(s"Deleting user with id: $userId")
    adminApi.delete(userId).map {
      case Right(result) =>
        logger.info("Successfully deleted user. Redirecting to search.")
        Redirect(routes.Search.search(searchQuery)).flashing("message" -> Messages("deleteUser.success", userId))
      case Left(error) =>
        logger.error(s"Failed to delete user. error: $error")
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("message" -> error.message)
    }
  }
}

class Delete @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with DeleteAction {

  def delete(searchQuery: String, userId: String) = AuthAction.async {
    doDelete(searchQuery, userId)
  }
}
