package auth

import com.gu.googleauth.UserIdentity
import play.api.libs.json.Json
import play.api.mvc.Session
import CSRF.ANTI_FORGERY_KEY
import services.GoogleAuthConf

object LoginSession {
  implicit class SessionOps(session: Session) {
    def loggedIn(identity: UserIdentity, loginOriginKey: String, expiry: Long) = {
      val shortLivedIdentity = identity.copy(exp = expiry)
      session + (UserIdentity.KEY -> Json.toJson(shortLivedIdentity).toString) - ANTI_FORGERY_KEY - loginOriginKey
    }

    def loginError = session - ANTI_FORGERY_KEY
  }
}
