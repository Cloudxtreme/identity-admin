package controllers

import models.{User, UserUpdateRequest}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import services.{CustomError, AdminApi}

import scala.concurrent.Future


class UpdateUserTest extends PlaySpec with OneServerPerSuite with MockitoSugar{

  val adminApiMock = mock[AdminApi]
  val controller = new SaveAction {override val adminApi: AdminApi = adminApiMock}
  val searchQuery = "search query"
  val userId = "1234"
  val userUpdateData = UserUpdateRequest("email@email.com", "username")
  val blankUser = User("","")
  val customError = CustomError("Test Error", "There is an error")

  "doSave" should {

    "redirect to search results with success message on success" in {
      when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
        Future.successful(Right(blankUser))
      )
      val result = controller.doSave(userId, userUpdateData, searchQuery)
      redirectLocation(result) mustEqual Some(routes.Search.search(searchQuery).url)
      flash(result).get("success") mustEqual Some("User has been updated")
    }

    "redirect to search results with error message on failure" in {
      when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
        Future.successful(Left(customError)))
      val result = controller.doSave(userId, userUpdateData, searchQuery)
      redirectLocation(result) mustEqual Some(routes.Search.search(searchQuery).url)
      flash(result).get("error") mustEqual Some("Test Error : There is an error")
    }
  }
}
