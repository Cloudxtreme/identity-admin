package controllers

import models.{Forms, User, UserUpdateRequest}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.data.Form
import play.api.test.Helpers._
import services.{CustomError, AdminApi}

import scala.concurrent.Future


class UpdateUserTest extends PlaySpec with OneServerPerSuite with MockitoSugar{

  val adminApiMock = mock[AdminApi]

  val controller = new SaveAction {override val adminApi: AdminApi = adminApiMock}

  val searchQuery = "search query"
  val userId = "1234"
  val userUpdateData = UserUpdateRequest("email@email.com","username")
  val blankUser = User("","", username=Some(""))
  val blankUserForm = Forms.createForm(blankUser)
  val customError = CustomError("Test Error", "There is an error")

  "update" should {

    "redirect to search results with success message on success" in {
      when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
        Future.successful(Right(blankUser))
      )
      val result = controller.update(userId,searchQuery, userUpdateData, blankUserForm)
      redirectLocation(result) mustEqual Some(routes.Search.search(searchQuery).url)
      flash(result).get("message") mustEqual Some("User has been updated")
    }

    "when form input is invalid return to the edit user page" in {
      when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
        Future.successful(Left(customError)))
      val result = controller.update(userId, searchQuery, userUpdateData, blankUserForm)
      status(result) mustEqual OK
    }
  }
}
