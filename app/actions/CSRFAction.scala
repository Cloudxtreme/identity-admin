package actions

import controllers.routes
import play.api.i18n.Messages
import play.api.mvc.{WrappedRequest, Result, Request, ActionRefiner}
import play.api.mvc.Results.Redirect
import play.api.libs.concurrent.Execution.Implicits._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future

class CSRFRequest[A](val csrfToken: String, request: Request[A]) extends WrappedRequest[A](request)

object CSRFAction extends ActionRefiner[Request, CSRFRequest] {
  private val ANTI_FORGERY_KEY = current.configuration.getString("identity-admin.antiForgeryKey").get
  private val loginUrl = routes.Login.login()

  override protected def refine[A](request: Request[A]): Future[Either[Result, CSRFRequest[A]]] = Future.successful {
    val csrfToken = request.session.get(ANTI_FORGERY_KEY)
    csrfToken match {
      case Some(token) => Right(new CSRFRequest[A](token, request))
      case None => Left(Redirect(loginUrl).flashing("error" -> Messages("login.missingAntiForgeryToken")))
    }
  }
}
