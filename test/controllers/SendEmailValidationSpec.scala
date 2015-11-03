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
      val result = controller.doSendEmailValidation(searchQuery, userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(searchQuery, userId).url)
      flash(result).get("message") mustEqual Some(s"Email validation for user $userId has been sent")
    }

    "redirect to edit user with error message on failure" in {
      val error = CustomError("Fatal error", "Boom")
      when(adminApiMock.sendEmailValidation(userId)).thenReturn(Future.successful(Left(error)))
      val result = controller.doSendEmailValidation(searchQuery, userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.AccessUser.getUser(searchQuery, userId).url)
      flash(result).get("message") mustEqual Some(error.message)
    }
  }

}
