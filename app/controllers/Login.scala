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
import services.GoogleGroups

import scala.concurrent.Future
import scala.util.Try

trait AuthActions extends Actions {
  val loginTarget: Call = routes.Login.loginAction()
  val authConfig = Login.googleAuthConfig
  val loginCall = routes.Login.login()
}

object Login extends Controller with AuthActions {
  val clientId = current.configuration.getString("identity-admin.google.clientId").get
  val clientSecret = current.configuration.getString("identity-admin.google.clientSecret").get
  val redirectUrl = current.configuration.getString("identity-admin.google.authorisationCallback").get
  val indexCall = routes.Application.index()
  val requiredGroups = Set("2FA_enforce", "useradmin")
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

  case class User(userIdentity: UserIdentity, isAdmin: Boolean)

  def oauth2Callback = CSRFAction.async { implicit request =>
    val session = request.session

    val safeValidatedUserIdentity: Future[UserIdentity] = {
      val t = Try(GoogleAuth.validatedUserIdentity(googleAuthConfig, request.csrfToken)) recover {
        case e: IllegalArgumentException => Future.failed(e)
      }
      t.get
    }

    val result = for {
      identity <- safeValidatedUserIdentity
      admin <- GoogleGroups.isUserAdmin(identity.email)
    } yield User(identity, admin)

    result map { _ match {
      case User(identity, true) => {
        val redirect = session.get(LOGIN_ORIGIN_KEY).map(Redirect(_)).getOrElse(Redirect(indexCall))
        redirect.withSession {
          session + (UserIdentity.KEY -> Json.toJson(identity).toString) - ANTI_FORGERY_KEY - LOGIN_ORIGIN_KEY
        }
      }
      case _ => {
        Redirect(loginCall).withSession(session - ANTI_FORGERY_KEY)
          .flashing("error" -> Messages("groups.failure"))
      }
    }} recover {
      case ex => {
        Redirect(loginCall).withSession(session - ANTI_FORGERY_KEY)
          .flashing("error" -> Messages("login.failure", ex.getMessage))
      }
    }
  }
}
