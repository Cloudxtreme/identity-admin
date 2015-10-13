package controllers

import csrf.CSRFAction
import csrf.CSRF.ANTI_FORGERY_KEY
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

object Login extends Controller with AuthActions {

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

    result map { _ match {
      case identity: UserIdentity => {
        val redirect = session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))
        redirect.withSession {
          session + (UserIdentity.KEY -> Json.toJson(identity).toString) - ANTI_FORGERY_KEY - LOGIN_ORIGIN_KEY
        }
      }
      case _ => {
        Redirect(loginCall).withSession(session - ANTI_FORGERY_KEY)
          .flashing("error" -> Messages("groups.failure", GoogleGroups.requiredGroups.mkString(", ")))
      }
    }} recover {
      case ex => {
        Redirect(loginCall).withSession(session - ANTI_FORGERY_KEY)
          .flashing("error" -> Messages("login.failure", ex.getMessage))
      }
    }
  }
}
