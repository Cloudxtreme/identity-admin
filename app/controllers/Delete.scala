package controllers

import javax.inject.Inject

import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Result, Controller}
import services.AdminApi
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import util.Logging
import models.Forms._

import scala.concurrent.Future

trait DeleteAction extends Controller with Logging{
  val adminApi: AdminApi

  private[controllers] def doDelete(userId: String): Future[Result] = {
    logger.info(s"Deleting user with id: $userId")
    adminApi.delete(userId).map {
      case Right(result) =>
        logger.info("Successfully deleted user. Redirecting to search.")
        Redirect(routes.Search.search()).flashing("message" -> Messages("deleteUser.success", userId))
      case Left(error) =>
        logger.error(s"Failed to delete user. error: $error")
        Redirect(routes.AccessUser.getUser(userId)).flashing("error" -> error.message)
    }
  }
}

class Delete @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with DeleteAction {

  def delete = AuthAction.async {  implicit request =>
    val id = idForm.bindFromRequest.get.id
    doDelete(id)
  }
}
