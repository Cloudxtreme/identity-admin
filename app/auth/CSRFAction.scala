package auth

import controllers.routes
import play.api.mvc._
import play.api.mvc.Results.Redirect
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import util.Logging

import scala.concurrent.Future

class CSRFRequest[A](val csrfToken: String, request: Request[A]) extends WrappedRequest[A](request)

object CSRFAction extends ActionRefiner[Request, CSRFRequest] with ActionBuilder[CSRFRequest] with Logging {

  override protected def refine[A](request: Request[A]): Future[Either[Result, CSRFRequest[A]]] = Future {
    val csrfToken = request.session.get(CSRF.ANTI_FORGERY_KEY)
    csrfToken match {
      case Some(token) => Right(new CSRFRequest[A](token, request))
      case None => {
        logger.info("Login Error: CSRFValidationFailed")
        Left(Redirect(routes.Login.login(Some(CSRF.CSRF_VALIDATION_FAILED))))
      }
    }
  }
}

object CSRF {
  val ANTI_FORGERY_KEY = "antiForgeryToken"
  val CSRF_VALIDATION_FAILED = "CSRFValidationFailed"
}