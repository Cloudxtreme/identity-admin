package services

import auth._
import com.gu.googleauth.{GoogleAuth, UserIdentity}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}
import scala.util.Try

object SafeGoogleAuth {

  val authConfig = GoogleAuthConf.googleAuthConfig

  def correctHostedDomain(identity: UserIdentity): Boolean = Some(identity.emailDomain) == authConfig.domain

  def validateUserIdentity[A](implicit request: CSRFRequest[A]): Future[Either[LoginError, UserIdentity]] = {
    val t = Try(GoogleAuth.validatedUserIdentity(GoogleAuthConf.googleAuthConfig, request.csrfToken).map {
      case identity: UserIdentity if correctHostedDomain(identity) => Right(identity)
      case identity: UserIdentity => Left(DomainValidationFailed())
      case _ => Left(IdentityValidationFailed())
    }.recover {
      case _ => Left(IdentityValidationFailed())
    }).recover {
      case e: IllegalArgumentException => Future(Left(CSRFValidationFailed()))
    }
    t.get
  }

  def validateUser[A](implicit request: CSRFRequest[A]): Future[Either[LoginError, UserIdentity]] = {
    validateUserIdentity.map {
      case Right(identity: UserIdentity) => Await.result(GoogleGroups.isUserAdmin(identity), 5.seconds)
      case _ => Left(IdentityValidationFailed())
    }
  }
}
