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
  val loginCall = routes.Login.login()
}

class Login @Inject() extends Controller with AuthActions {

  val indexCall = routes.Application.index()

  def login = Action { request =>
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
        redirect.withSession { session.loggedIn(identity, LOGIN_ORIGIN_KEY, (System.currentTimeMillis + GoogleAuthConf.sessionMaxAge) / 1000)}
      }
      case _ => {
        val redirect = loginErrorRedirect("groups.failure", GoogleGroups.requiredGroupsMsg)
        redirect.withSession { session.loginError }
      }
    } recover {
      case ex => {
        val redirect = loginErrorRedirect("login.failure", ex.getMessage)
        redirect.withSession { session.loginError }
      }
    }
  }

  private def successfulLoginRedirect(session: Session): Result =
    session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))

  private def loginErrorRedirect(errorType: String, additionalMessage: String): Result =
    Redirect(loginCall).flashing("error" -> Messages(errorType, additionalMessage))
}
