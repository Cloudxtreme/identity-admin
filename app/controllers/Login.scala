package controllers

import javax.inject.Inject

import auth.{IdentityValidationFailed, GroupsValidationFailed, LoginError, CSRFAction}
import auth.CSRF.ANTI_FORGERY_KEY
import auth.LoginSession.SessionOps
import config.Config
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.googleauth._
import play.api.Play.current
import services.{SafeGoogleAuth, GoogleAuthConf, GoogleGroups}

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = GoogleAuthConf.googleAuthConfig
}

class Login @Inject() (conf: Config) extends Controller with AuthActions {

  val indexCall = routes.Application.index()
  val contactEmail = conf.errorEmail

  def login(loginError: Option[LoginError]) = Action { request =>
    Ok(views.html.login(loginError, Some(contactEmail)))
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
        val redirect = loginErrorRedirect(GroupsValidationFailed())
        redirect.withSession { session.loginError }
      }
    } recover {
      case ex => {
        val redirect = loginErrorRedirect(IdentityValidationFailed())
        redirect.withSession { session.loginError }
      }
    }
  }

  private def successfulLoginRedirect(session: Session): Result =
    session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))

  private def loginErrorRedirect(loginError: LoginError): Result = {
    Redirect(routes.Login.login(Some(loginError)))
  }
}
