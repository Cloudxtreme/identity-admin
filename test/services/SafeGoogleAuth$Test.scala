package services

import auth.CSRFRequest
import com.gu.googleauth.{UserIdentity, GoogleAuthConfig}
import org.scalatest.concurrent.{ScalaFutures, AsyncAssertions}
import org.scalatest.{ParallelTestExecution, Matchers, FlatSpec}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SafeGoogleAuth$Test extends FlatSpec with Matchers with ScalaFutures with ParallelTestExecution with AsyncAssertions {

  val identity = UserIdentity("sub","test.com","firstName","lastName",1234,None)
  val googleAuthConfig = GoogleAuthConfig("1", "ssh", "http://test.com", Some("test.com"), None, false)
  val request = FakeRequest()
  implicit val app = FakeApplication()
  implicit val csrfAction = new CSRFRequest("token", request)

  behavior of "SafeGoogleAuth$Test"

  it should "validateUserIdentity" in {
    running(app) {
      whenReady(SafeGoogleAuth.validateUserIdentity(validatedUserIdentity, googleAuthConfig, "test")) { result =>
        result should be(Right(identity))
      }
    }
  }

  def validatedUserIdentity(config: GoogleAuthConfig, expectedAntiForgeryToken: String): Future[UserIdentity] = Future(identity)

}
