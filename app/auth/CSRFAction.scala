package auth

import controllers.routes
import play.api.i18n.Messages
import play.api.mvc._
import play.api.mvc.Results.Redirect
import play.api.libs.concurrent.Execution.Implicits._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future

class CSRFRequest[A](val csrfToken: String, request: Request[A]) extends WrappedRequest[A](request)

object CSRFAction extends ActionRefiner[Request, CSRFRequest] with ActionBuilder[CSRFRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, CSRFRequest[A]]] = Future {
    val csrfToken = request.session.get(CSRF.ANTI_FORGERY_KEY)
    csrfToken match {
      case Some(token) => Right(new CSRFRequest[A](token, request))
      case None => Left(Redirect(routes.Login.login("csrf")).flashing("error" -> Messages("login.missingAntiForgeryToken")))
    }
  }
}

object CSRF {
  val ANTI_FORGERY_KEY = "antiForgeryToken"
}