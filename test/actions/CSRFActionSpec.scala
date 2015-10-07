package actions

import play.api.test.{FakeApplication, FakeRequest}
import collection.mutable.Stack
import org.specs2.mutable._
import play.api.mvc.Action
import play.api.mvc.Results.Ok
import play.api.test.Helpers._
import CSRFRequest.CSRFAction

class CSRFActionSpec extends Specification {

  implicit val app = FakeApplication(additionalConfiguration = Map("identity-admin.antiForgeryKey" -> "antiForgeryToken"))

  "CSRFAction" should {
    "stop a request if there is no CSRF token present" in {
      running(app) {
        val action = (Action andThen CSRFAction) {
          request => Ok
        }

        val request = FakeRequest()

        val result = call(action, request)

        status(result) mustEqual 303
        redirectLocation(result) mustEqual Some("/login")
        flash(result).get("error") mustEqual Some("Anti forgery token missing in session")
      }
    }
  }

  "CSRFAction" should {
    "allow a request to go through and return a CSRF token if it is present" in {
      running(app) {
        val action = (Action andThen CSRFAction) {
          request => {
            val token = request.csrfToken
            Ok(token)
          }
        }

        val ANTI_FORGERY_KEY = "antiForgeryToken"
        val request = FakeRequest().withSession(ANTI_FORGERY_KEY -> "abcd")

        val result = call(action, request)

        status(result) mustEqual 200
        contentAsString(result) mustEqual "abcd"
        flash(result).get("error") mustEqual None
      }
    }
  }
}
