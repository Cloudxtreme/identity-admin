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

trait SendEmailValidationAction extends Controller with Logging{
  val adminApi: AdminApi

  private[controllers] def doSendEmailValidation(searchQuery: String, userId: String): Future[Result] = {
    logger.info(s"Sending email validation for user with id: $userId")
    adminApi.sendEmailValidation(userId).map {
      case Right(result) =>
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("message" -> Messages("sendEmailValidation.success", userId))
      case Left(error) =>
        logger.error(s"Failed to send email validation for user with id: $userId. error: $error")
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("message" -> error.message)
    }
  }
}

class SendEmailValidation @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with DeleteAction {

  def sendEmailValidation(searchQuery: String, userId: String) = AuthAction.async {
    doDelete(searchQuery, userId)
  }
}