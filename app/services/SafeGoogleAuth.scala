package services

import auth._
import com.gu.googleauth.{GoogleAuthConfig, GoogleAuth, UserIdentity}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}
import scala.util.Try
import play.api.Play.current

object SafeGoogleAuth {

  def correctHostedDomain(identity: UserIdentity, googleAuthConfig: GoogleAuthConfig): Boolean = Some(identity.emailDomain) == googleAuthConfig.domain

  def validateUserIdentity[A](f: (GoogleAuthConfig, String) => Future[UserIdentity], googleAuthConfig: GoogleAuthConfig, expectedAntiForgeryToken: String)
                             (implicit request: CSRFRequest[A]): Future[Either[LoginError, UserIdentity]] = {
    val t = Try(f(googleAuthConfig, expectedAntiForgeryToken).map { identity =>
      if (correctHostedDomain(identity, googleAuthConfig)) Right(identity)
      else Left(DomainValidationFailed())
    }.recover {
      case _ => Left(IdentityValidationFailed())
    }).recover {
      case e: IllegalArgumentException => Future(Left(CSRFValidationFailed()))
    }
    t.get
  }

  def validateUser[A](googleAuthConfig: GoogleAuthConfig)(implicit request: CSRFRequest[A]): Future[Either[LoginError, UserIdentity]] = {
    validateUserIdentity(GoogleAuth.validatedUserIdentity(_, _), googleAuthConfig, request.csrfToken).map {
      case Right(identity: UserIdentity) => Await.result(GoogleGroups.isUserAdmin(identity), 5.seconds)
      case _ => Left(IdentityValidationFailed())
    }
  }
}