package googlegroups

import com.gu.googleauth.UserIdentity
import googlegroups.DirectoryApiResponses.AccessOk
import googlegroups.DirectoryApiResponses.AccessError
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait GoogleGroups {

  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]]
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]]

  def validate(user: UserIdentity): Future[Either[AccessError, UserIdentity]] = {
    val check2FA = validate2FA(user.email)
    val checkAdmin = validateAdmin(user.email)

    Future.sequence(Seq(check2FA, checkAdmin)).map {
      case Seq(Right(fa), Right(admin)) => Right(user)
      case errs => {
        val allErrs = errs.collect { case Left(err) => err }.foldLeft(AccessError(""))((a,b) => b ++ a)
        Left(allErrs)
      }
    }
  }
}

object GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]] = ???
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]] = ???
}
