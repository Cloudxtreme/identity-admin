package controllers

import actions.CSRFAction
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.googleauth._
import play.api.Play.current

import scala.concurrent.Future

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = Login.googleAuthConfig
  val loginUrl = routes.Login.login()
}

object Login extends Controller with AuthActions {
  val ANTI_FORGERY_KEY = current.configuration.getString("identity-admin.antiForgeryKey").get
  val clientId = current.configuration.getString("identity-admin.google.clientId").get
  val clientSecret = current.configuration.getString("identity-admin.google.clientSecret").get
  val redirectUrl = current.configuration.getString("identity-admin.google.authorisationCallback").get
  val indexUrl = routes.Application.index()
  val googleAuthConfig =
    GoogleAuthConfig(
      clientId = clientId,
      clientSecret = clientSecret,
      redirectUrl = redirectUrl,
      domain = Some("guardian.co.uk")
    )

  def login = Action { request =>
    val error = request.flash.get("error")
    Ok(views.html.login(error))
  }

  def loginAction = Action.async { implicit request =>
    val antiForgeryToken = GoogleAuth.generateAntiForgeryToken()
    GoogleAuth.redirectToGoogle(googleAuthConfig, antiForgeryToken).map {
      _.withSession {
        request.session + (ANTI_FORGERY_KEY -> antiForgeryToken)
      }
    }
  }

  def oauth2Callback = (Action andThen CSRFAction).async { implicit request =>
    val session = request.session
    val newSession = session - ANTI_FORGERY_KEY - LOGIN_ORIGIN_KEY

    GoogleAuth.validatedUserIdentity(googleAuthConfig, request.csrfToken).map { identity =>
      val redirect = session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexUrl))
      redirect.withSession {
        newSession + (UserIdentity.KEY -> Json.toJson(identity).toString)
      }
    } recover { case t: IllegalArgumentException =>
      Redirect(loginUrl).withSession(newSession)
        .flashing("error" -> Messages("login.failure", t.getMessage))
    }
  }
}
