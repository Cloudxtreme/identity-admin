package auth

import com.gu.googleauth.UserIdentity
import play.api.libs.json.Json
import play.api.mvc.Session
import CSRF.ANTI_FORGERY_KEY

object LoginSession {
  implicit class SessionOps(session: Session) {
    def loggedIn(identity: UserIdentity, loginOriginKey: String) =
      session + (UserIdentity.KEY -> Json.toJson(identity).toString) - ANTI_FORGERY_KEY - loginOriginKey

    def loginError = session - ANTI_FORGERY_KEY
  }
}
