package services

import auth._
import com.gu.googleauth.{GoogleAuthConfig, GoogleAuth, UserIdentity}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

import scala.concurrent.{Future}
import scala.util.Try
import play.api.Play.current

import scalaz.{\/-, -\/, \/}

object SafeGoogleAuth {
  
  def validateUserIdentity[A](f: (GoogleAuthConfig, String) => Future[UserIdentity], googleAuthConfig: GoogleAuthConfig, expectedAntiForgeryToken: String)
                             (implicit request: CSRFRequest[A]): Future[\/[LoginError, UserIdentity]] = {
    val t = Try(f(googleAuthConfig, expectedAntiForgeryToken).map{ identity =>
      \/-(identity)
    }.recover {
      case _ =>  -\/(IdentityValidationFailed())
    })
    t.getOrElse(Future(-\/(CSRFValidationFailed())))
  }

  def validateUser[A](googleAuthConfig: GoogleAuthConfig)(implicit request: CSRFRequest[A]): Future[\/[LoginError, UserIdentity]] =
    validateUserIdentity(GoogleAuth.validatedUserIdentity(_, _), googleAuthConfig, request.csrfToken)
}