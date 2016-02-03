package auth

import com.gu.googleauth.UserIdentity
import model.AdminIdentity
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.Session
import auth.LoginSession.SessionOps

class LoginSessionSpec extends FlatSpec with Matchers {
  val session = Session(Map(CSRF.ANTI_FORGERY_KEY -> "abcd", "loginOriginUrl" -> "loginUrl"))
  val adminIdentity = AdminIdentity(UserIdentity("sub","email","firstName","lastName",1234,None))

  "loggedIn session" should "only contain the admin user's identity" in {
    val expectedIdentity = adminIdentity.userIdentity.copy(exp = 6789)
    session.loggedIn(adminIdentity, "loginOriginUrl", 6789) should be (
      Session(Map("identity" -> expectedIdentity.asJson))
    )
  }

  "loginError session" should "only contain the login origin key" in {
    session.loginError should be (Session(Map("loginOriginUrl" -> "loginUrl")))
  }
}