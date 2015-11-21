package controllers

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import services.{AdminApi, CustomError}

import scala.concurrent.Future

class SendEmailValidationSpec extends PlaySpec with OneServerPerSuite with MockitoSugar {

  val adminApiMock = mock[AdminApi]

  val controller = new SendEmailValidationAction {
    override val adminApi: AdminApi = adminApiMock
  }

  "doSendEmailValidation" should {
    val userId = "1234"
    val searchQuery = "search query"

    "redirect to edit page on success" in {
      when(adminApiMock.sendEmailValidation(userId)).thenReturn(Future.successful(Right(true)))
      val result = controller.doSendEmailValidation(userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(userId).url)
      flash(result).get("message") mustEqual Some(s"Email validation for user $userId has been sent")
    }

    "redirect to edit user with customerror message on failure" in {
      val error = CustomError("Fatal customerror", "Boom")
      when(adminApiMock.sendEmailValidation(userId)).thenReturn(Future.successful(Left(error)))
      val result = controller.doSendEmailValidation(userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(userId).url)
      flash(result).get("error") mustEqual Some(error.message)
    }
  }

  "doValidateEmail" should {
    val userId = "1234"
    val searchQuery = "search query"

    "redirect to edit page on success" in {
      when(adminApiMock.validateEmail(userId)).thenReturn(Future.successful(Right(true)))
      val result = controller.doValidateEmail(userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(userId).url)
      flash(result).get("message") mustEqual Some(s"Email has been validated for user $userId")
    }

    "redirect to edit user with customerror message on failure" in {
      val error = CustomError("Fatal customerror", "Boom")
      when(adminApiMock.validateEmail(userId)).thenReturn(Future.successful(Left(error)))
      val result = controller.doValidateEmail(userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(userId).url)
      flash(result).get("error") mustEqual Some(error.message)
    }
  }

}
