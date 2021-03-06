package controllers

import javax.inject.Inject

import auth._
import auth.CSRF.ANTI_FORGERY_KEY
import auth.LoginSession.SessionOps
import config.Config
import model.AdminIdentity
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.googleauth._
import play.api.Play.current
import services.{GoogleAuthConf, GoogleGroups}
import services.SafeGoogleAuth._
import util.Logging

import scala.concurrent.Future
import scalaz.{-\/, \/-, EitherT}
import scalaz.std.scalaFuture._

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = GoogleAuthConf.googleAuthConfig
}

class Login @Inject() (conf: Config) extends Controller with AuthActions with Logging {

  val indexCall = routes.Application.index()
  val contactEmail = conf.errorEmail

  def login(loginError: Option[LoginError]) = Action { request =>
    Ok(views.html.login(loginError, Some(contactEmail)))
  }

  def loginAction = Action.async { implicit request =>
    val antiForgeryToken = GoogleAuth.generateAntiForgeryToken()
    GoogleAuth.redirectToGoogle(authConfig, antiForgeryToken).map {
      //Creates a new session
      _.withSession {
        (ANTI_FORGERY_KEY -> antiForgeryToken)
      }
    }
  }

  def oauth2Callback = CSRFAction.async { implicit request =>
    val session = request.session

    type FutureEither[A] = EitherT[Future, LoginError, A]

    val result: FutureEither[AdminIdentity] = for {
      identity <- EitherT(validateUser(authConfig))
      adminUser <- EitherT(GoogleGroups.validateUserAdmin(identity))
    } yield adminUser

    result.run map {
      case \/-(adminIdentity) => {
        val redirect = successfulLoginRedirect(session)
        val sessionLengthInSeconds = (System.currentTimeMillis + GoogleAuthConf.sessionMaxAge) / 1000
        redirect.withSession { session.loggedIn(adminIdentity, LOGIN_ORIGIN_KEY, sessionLengthInSeconds)}
      }
      case -\/(error) => {
        val redirect = loginErrorRedirect(error)
        redirect.withSession { session.loginError }
      }
    } recover {
      case ex: Throwable => {
        logger.error("UnexpectedError", ex)
        val redirect = loginErrorRedirect(UnexpectedError())
        redirect.withSession { session.loginError }
      }
    }
  }

  private def successfulLoginRedirect(session: Session): Result =
    session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))

  private def loginErrorRedirect(loginError: LoginError): Result =
    Redirect(routes.Login.login(Some(loginError)))
}
