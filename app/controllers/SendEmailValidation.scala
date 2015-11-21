package controllers


import javax.inject.Inject

import models.Forms._
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
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("error" -> error.message)
    }
  }

  private[controllers] def doValidateEmail(searchQuery: String, userId: String): Future[Result] = {
    logger.info(s"Validating email for user with id: $userId")
    adminApi.validateEmail(userId).map {
      case Right(result) =>
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("message" -> Messages("validateEmail.success", userId))
      case Left(error) =>
        logger.error(s"Failed to validate email for user with id: $userId. error: $error")
        Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("error" -> error.message)
    }
  }
}

class SendEmailValidation @Inject() (val adminApi: AdminApi) extends Controller with AuthActions with SendEmailValidationAction {

  def sendEmailValidation = AuthAction.async { implicit request =>
    val id = idForm.bindFromRequest.get.id
    val searchQuery = request.session.get("query").getOrElse("")
    request.session - "query"
    doSendEmailValidation(searchQuery, id)
  }

  def validateEmail = AuthAction.async {implicit request =>
    val id = idForm.bindFromRequest.get.id
    val searchQuery = request.session.get("query").getOrElse("")
    request.session - "query"
    doValidateEmail(searchQuery, id)
  }
}
