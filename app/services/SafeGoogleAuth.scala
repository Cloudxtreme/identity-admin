package services

import com.gu.googleauth.{GoogleAuthConfig, UserIdentity, GoogleAuth}
import auth.{IdentityValidationFailed, LoginError, CSRFRequest}
import play.api.mvc.RequestHeader

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

object SafeGoogleAuth {

  val authConfig = GoogleAuthConf.googleAuthConfig

  def correctHostedDomain(identity: UserIdentity): Future[Boolean] =
    Future.successful(Some(identity.emailDomain) == authConfig.domain)

  def validatedUserIdentity[A](implicit request: CSRFRequest[A]): Future[UserIdentity] = {
    val t = Try(GoogleAuth.validatedUserIdentity(GoogleAuthConf.googleAuthConfig, request.csrfToken)) recover {
      case e: IllegalArgumentException => Future.failed(e)
    }
    t.get
  }

}
