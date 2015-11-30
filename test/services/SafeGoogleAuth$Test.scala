package services

import auth.{IdentityValidationFailed, CSRFValidationFailed, DomainValidationFailed, CSRFRequest}
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
      whenReady(SafeGoogleAuth.validateUserIdentity(validUserIdentity, googleAuthConfig, "test")) { result =>
        result should be(Right(identity))
      }
      whenReady(SafeGoogleAuth.validateUserIdentity(incorrectDomainUserIdentity, googleAuthConfig, "test")) { result =>
        result should be(Left(DomainValidationFailed()))
      }
      whenReady(SafeGoogleAuth.validateUserIdentity(futureFailed, googleAuthConfig, "test")) { result =>
        result should be(Left(IdentityValidationFailed()))
      }
      whenReady(SafeGoogleAuth.validateUserIdentity(csrfError, googleAuthConfig, "test")) { result =>
        result should be(Left(CSRFValidationFailed()))
      }
    }
  }

  def validUserIdentity(config: GoogleAuthConfig, token: String): Future[UserIdentity] = Future(identity)

  def incorrectDomainUserIdentity(config: GoogleAuthConfig, token: String): Future[UserIdentity] = Future(identity.copy(email = "test1.com"))

  def futureFailed(config: GoogleAuthConfig, token: String): Future[UserIdentity] = Future.failed(new Exception)

  def csrfError(config: GoogleAuthConfig, token: String): Future[UserIdentity] = throw new IllegalArgumentException

}
