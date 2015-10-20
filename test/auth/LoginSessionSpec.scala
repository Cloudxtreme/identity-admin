package auth

import com.gu.googleauth.UserIdentity
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.Session
import auth.LoginSession.SessionOps

class LoginSessionSpec extends FlatSpec with Matchers {
  val session = Session(Map(CSRF.ANTI_FORGERY_KEY -> "abcd", "loginOriginUrl" -> "loginUrl"))
  val identity = UserIdentity("sub","email","firstName","lastName",1234,None)

  "loggedIn session" should "only contain the user's identity" in {
    session.loggedIn(identity, "loginOriginUrl") should be (Session(Map("identity" -> identity.asJson)))
  }
  "loginError session" should "only contain the login origin key" in {
    session.loginError should be (Session(Map("loginOriginUrl" -> "loginUrl")))
  }
}