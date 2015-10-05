package controllers

import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.googleauth._
import play.api.Play.current

import scala.concurrent.Future

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = Login.googleAuthConfig
}

object Login extends Controller with AuthActions {
  val ANTI_FORGERY_KEY = "antiForgeryToken"
  val clientId = current.configuration.getString("identity-admin.google.clientId").get
  val clientSecret = current.configuration.getString("identity-admin.google.clientSecret").get
  val redirectUrl = current.configuration.getString("identity-admin.google.authorisationCallback").get

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

  def oauth2Callback = Action.async { implicit request =>
    val session = request.session
    session.get(ANTI_FORGERY_KEY).map(auth(_: String, session: Session)).getOrElse(forgery)
  }

  def forgery = Future.successful(Redirect(routes.Login.login())
    .flashing("error" -> "Anti forgery token missing in session"))

  def auth(token: String, session: Session) = {
    GoogleAuth.validatedUserIdentity(googleAuthConfig, token).map { identity =>
      val redirect = session.get(LOGIN_ORIGIN_KEY) match {
        case Some(url) => Redirect(url)
        case None => Redirect(routes.Application.index())
      }
      redirect.withSession {
        session + (UserIdentity.KEY -> Json.toJson(identity).toString) - ANTI_FORGERY_KEY - LOGIN_ORIGIN_KEY
      }
    } recover {
      case t =>
        Redirect(routes.Login.login())
          .withSession(session - ANTI_FORGERY_KEY)
          .flashing("error" -> s"Login failure: ${t.toString}")
    }
  }

}
