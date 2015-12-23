package controllers

import models.{SearchResponse, UserSummary}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import services.AdminApi
import org.mockito.Mockito._
import play.filters.csrf.CSRF

import scala.concurrent.Future
import play.api.test.Helpers._

class SearchSpec extends PlaySpec with OneServerPerSuite with MockitoSugar {

  val adminApiMock = mock[AdminApi]
  val fakeApp = FakeApplication(additionalConfiguration = Map("play.crypto.secret" -> "test"))

  class TestController extends SearchAction
  val controller = new TestController

  private def createResult(id: String): UserSummary =
    UserSummary(id = id, email = "email", username = None, firstName = None, lastName = None, creationDate = None, lastActiveIpAddress = None, lastActivityDate = None, registrationIp = None)


  "search" should {
    val query = "query"

    "render search results page when more than one result" in {
      running(fakeApp) {
        val r1 = createResult("1")
        val r2 = createResult("2")
        val sr = SearchResponse(total = 2, hasMore = false, results = Seq(r1, r2))
        when(adminApiMock.getUsers(query)).thenReturn(Future.successful(Right(sr)))
        implicit val request = FakeRequest().withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        val result = controller.doSearch(adminApiMock, query)

        status(result) mustEqual OK
        contentAsString(result) must include("Accounts matching your search criteria...")
      }
    }

    "render search results page when zero results" in {
      running(fakeApp) {
        val sr = SearchResponse(total = 0, hasMore = false, results = Nil)
        when(adminApiMock.getUsers(query)).thenReturn(Future.successful(Right(sr)))
        implicit val request = FakeRequest().withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        val result = controller.doSearch(adminApiMock, query)

        status(result) mustEqual OK
        contentAsString(result) must include("Your search query did not match any records.")
      }
    }

    "redirect to edit user when one result" in {
      running(fakeApp) {
        val r1 = createResult("123")
        val sr = SearchResponse(total = 1, hasMore = false, results = Seq(r1))
        when(adminApiMock.getUsers(query)).thenReturn(Future.successful(Right(sr)))
        implicit val request = FakeRequest().withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
        val result = controller.doSearch(adminApiMock, query)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustEqual Some(routes.AccessUser.getUser("123").url)
      }
    }
  }

}
