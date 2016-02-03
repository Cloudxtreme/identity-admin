package auth

import com.gu.googleauth.UserIdentity
import model.AdminIdentity
import play.api.libs.json.Json
import play.api.mvc.Session
import CSRF.ANTI_FORGERY_KEY

object LoginSession {
  implicit class SessionOps(session: Session) {
    def loggedIn(identity: AdminIdentity, loginOriginKey: String, expiry: Long) = {
      val shortLivedIdentity = identity.userIdentity.copy(exp = expiry)
      session + (UserIdentity.KEY -> Json.toJson(shortLivedIdentity).toString) - ANTI_FORGERY_KEY - loginOriginKey
    }

    def loginError = session - ANTI_FORGERY_KEY
  }
}
