package googlegroups

import com.gu.googleauth.UserIdentity
import googlegroups.DirectoryApiResponses._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

object Invalid2FA extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Left(no2FAMembership))
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Right(AccessOk))
}

object InvalidAdmin extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Right(AccessOk))
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Left(noAdminMembership))
}

object InvalidGroups extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Left(no2FAMembership))
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Left(noAdminMembership))
}

object ValidGroups extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Right(AccessOk))
  def validateAdmin(emailAddress: String): Future[Either[AccessError, AccessOk]] = Future(Right(AccessOk))
}

class GoogleGroupsTests extends FlatSpec with Matchers with ScalaFutures {
  val user = new UserIdentity(
    sub = "sub",
    email = "email",
    firstName = "firstName",
    lastName = "lastName",
    exp = 123,
    avatarUrl = None
  )

  "validate" should "return an error if the user is not present in the 2FA group" in {
    whenReady(Invalid2FA.validate(user)) { res =>
      res should be(Left(no2FAMembership))
    }
  }

  it should "return an error if he user is not present in the admin group" in {
    whenReady(InvalidAdmin.validate(user)) { res =>
      res should be(Left(noAdminMembership))
    }
  }

  it should "return two error messages if the user is not present in any group" in {
    whenReady(InvalidGroups.validate(user)) { res =>
     res should be(Left(AccessError(noAdminMembership.msg ++ " & " ++ no2FAMembership.msg)))
    }
  }

  it should "return a user if the user is present in both groups" in {
    whenReady(ValidGroups.validate(user)) { res =>
      res should be(Right(user))
    }
  }
}
