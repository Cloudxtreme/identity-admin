package services

import auth.CSRFRequest
import com.gu.googleauth.{UserIdentity, GoogleAuthConfig}
import org.scalatest.FlatSpec
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}
import play.filters.csrf.CSRFAction

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SafeGoogleAuth$Test extends FlatSpec {

  val identity = UserIdentity("sub","email","firstName","lastName",1234,None)
  val googleAuthConfig = GoogleAuthConfig("1", "ssh", "http://oauthcallback.com", None, None, false)
  val request = FakeRequest()
  implicit val app = FakeApplication()
  implicit val csrfAction = new CSRFRequest("token", request)

  behavior of "SafeGoogleAuth$Test"

  it should "validateUserIdentity" in {
    running(app) {
      SafeGoogleAuth.validateUserIdentity(validatedUserIdentity, googleAuthConfig, "test")
    }
  }

  def validatedUserIdentity(config: GoogleAuthConfig, expectedAntiForgeryToken: String): Future[UserIdentity] = Future(identity)

}
