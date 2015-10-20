package services

import com.gu.googleauth.{UserIdentity, GoogleAuth}
import auth.CSRFRequest
import play.api.mvc.RequestHeader

import scala.concurrent.Future
import scala.util.Try
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

object SafeGoogleAuth {

  def validatedUserIdentity[A](implicit request: CSRFRequest[A]): Future[UserIdentity] = {
    val t = Try(GoogleAuth.validatedUserIdentity(GoogleAuthConf.googleAuthConfig, request.csrfToken)) recover {
      case e: IllegalArgumentException => Future.failed(e)
    }
    t.get
  }

}
