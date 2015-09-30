package googlegroups

import com.gu.googleauth.UserIdentity
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

object Invalid2FA extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[String, String]] = Future(Left("User Not In 2FA Group."))
  def validateAdmin(emailAddress: String): Future[Either[String, String]] = Future(Right("Ok"))
}

object InvalidAdmin extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[String, String]] = Future(Right("Ok"))
  def validateAdmin(emailAddress: String): Future[Either[String, String]] = Future(Left("User Not In Admin Group."))
}

object InvalidGroups extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[String, String]] = Future(Left("User Not In 2FA Group."))
  def validateAdmin(emailAddress: String): Future[Either[String, String]] = Future(Left("User Not In Admin Group."))
}

object ValidGroups extends GoogleGroups {
  def validate2FA(emailAddress: String): Future[Either[String, String]] = Future(Right("Ok"))
  def validateAdmin(emailAddress: String): Future[Either[String, String]] = Future(Right("Ok"))
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
      res should be(Left("User Not In 2FA Group."))
    }
  }

  it should "return an error if the user is not present in the admin group" in {
    whenReady(InvalidAdmin.validate(user)) { res =>
      res should be(Left("User Not In Admin Group."))
    }
  }

  it should "return two error messages if the user is not present in any group" in {
    whenReady(InvalidGroups.validate(user)) { res =>
      res should be(Left("User Not In 2FA Group.User Not In Admin Group."))
    }
  }

  it should "return a user if the user is present in both groups" in {
    whenReady(ValidGroups.validate(user)) { res =>
      res should be(Right(user))
    }
  }
}
