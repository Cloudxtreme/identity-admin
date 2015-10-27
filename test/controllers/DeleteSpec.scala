package controllers

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import services.{CustomError, AdminApi}

import scala.concurrent.Future

class DeleteSpec extends PlaySpec with OneServerPerSuite with MockitoSugar {

  val adminApiMock = mock[AdminApi]

  val controller = new DeleteAction {
    override val adminApi: AdminApi = adminApiMock
  }

  "doDelete" should {
    val userId = "1234"
    val searchQuery = "search query"

    "redirect to search results on success" in {
      when(adminApiMock.delete(userId)).thenReturn(Future.successful(Right(true)))
      val result = controller.doDelete(searchQuery, userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.Search.search(searchQuery).url)
      flash(result).get("message") mustEqual Some(s"User $userId has been deleted")
    }

    "redirect to edit user with error message on failure" in {
      val error = CustomError("Fatal error", "Boom")
      when(adminApiMock.delete(userId)).thenReturn(Future.successful(Left(error)))
      val result = controller.doDelete(searchQuery, userId)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.Application.getEditUserPage(searchQuery, userId).url)
      flash(result).get("error") mustEqual Some(error.message)
    }
  }

}
