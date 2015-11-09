package controllers

import javax.inject.Inject

import auth.CSRFAction
import auth.CSRF.ANTI_FORGERY_KEY
import auth.LoginSession.SessionOps
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.googleauth._
import play.api.Play.current
import services.{SafeGoogleAuth, GoogleAuthConf, GoogleGroups}

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = GoogleAuthConf.googleAuthConfig
}

class Login @Inject() extends Controller with AuthActions {
  case class LoginError(queryParam: String, translationKey: String, additionalMessage: String)

  val indexCall = routes.Application.index()

  def login(errorType: String) = Action { request =>
    val error = request.flash.get("error")
    Ok(views.html.login(error))
  }

  def loginAction = Action.async { implicit request =>
    val antiForgeryToken = GoogleAuth.generateAntiForgeryToken()
    GoogleAuth.redirectToGoogle(authConfig, antiForgeryToken).map {
      _.withSession {
        request.session + (ANTI_FORGERY_KEY -> antiForgeryToken)
      }
    }
  }

  def oauth2Callback = CSRFAction.async { implicit request =>
    val session = request.session

    val result = for {
      identity <- SafeGoogleAuth.validatedUserIdentity
      admin <- GoogleGroups.isUserAdmin(identity.email)
    } yield if (admin) identity

    result map {
      case identity: UserIdentity => {
        val redirect = successfulLoginRedirect(session)
        val sessionLengthInSeconds = (System.currentTimeMillis + GoogleAuthConf.sessionMaxAge) / 1000
        redirect.withSession { session.loggedIn(identity, LOGIN_ORIGIN_KEY, sessionLengthInSeconds)}
      }
      case _ => {
        val error = LoginError("groups", "groups.failure", GoogleGroups.requiredGroupsMsg)
        val redirect = loginErrorRedirect(error)
        redirect.withSession { session.loginError }
      }
    } recover {
      case ex => {
        val error = LoginError("login", "login.failure", ex.getMessage)
        val redirect = loginErrorRedirect(error)
        redirect.withSession { session.loginError }
      }
    }
  }

  private def successfulLoginRedirect(session: Session): Result =
    session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))

  private def loginErrorRedirect(error: LoginError): Result =
    Redirect(routes.Login.login(error.queryParam)).flashing("error" -> Messages(error.translationKey, error.additionalMessage))
}
