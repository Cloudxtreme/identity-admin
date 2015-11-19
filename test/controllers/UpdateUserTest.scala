package controllers

import config.Config
import models.{Forms, User, UserUpdateRequest}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import play.filters.csrf.CSRF
import services.{CustomError, AdminApi}

import scala.concurrent.Future

class UpdateUserTest extends PlaySpec with OneServerPerSuite with MockitoSugar{

  val fakeApp = FakeApplication(additionalConfiguration = Map("play.crypto.secret" -> "test"))
  val adminApiMock = mock[AdminApi]
  val controller = new SaveAction {
    override val adminApi: AdminApi = adminApiMock
    override val conf = new Config {
      val baseUrl = "baseUrl"
      val baseRootUrl = "baseRootUrl"
      val errorEmail = "errorEmail"
      override val publicProfileUrl: String = "publicProfileUrl"
      override val avatarUrl: String = "avatarUrl"
    }
    override val publicProfileUrl: String = conf.publicProfileUrl
    override val avatarUrl: String = conf.avatarUrl
  }

  val searchQuery = "search query"
  val userId = "1234"
  val userUpdateData = UserUpdateRequest("email@email.com","username")
  val blankUser = User("","", username=Some(""))
  val blankUserForm = Forms.createForm(blankUser)
  val customError = CustomError("Test Error", "There is an error")

  "update" should {

    "redirect to search results with success message on success" in {
      running(fakeApp) {
        when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
          Future.successful(Right(blankUser))
        )
        val request = FakeRequest().withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        val result = controller.update(userId, searchQuery, userUpdateData, blankUserForm)(request)
        redirectLocation(result) mustEqual Some(routes.Search.search(searchQuery).url)
        flash(result).get("message") mustEqual Some("User has been updated")
      }
    }

    "when form input is invalid return to the edit user page" in {
      running(fakeApp) {
        val request = FakeRequest().withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        when(adminApiMock.updateUserData(userId, userUpdateData)).thenReturn(
          Future.successful(Left(customError)))
        val result = controller.update(userId, searchQuery, userUpdateData, blankUserForm)(request)
        status(result) mustEqual OK
      }
    }
  }
}
