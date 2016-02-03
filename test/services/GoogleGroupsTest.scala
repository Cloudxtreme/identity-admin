package services

import auth.GroupsValidationFailed
import com.gu.googleauth.UserIdentity
import model.AdminIdentity
import org.scalatest.{ParallelTestExecution, Matchers, FlatSpec}
import org.scalatest.concurrent.{AsyncAssertions, ScalaFutures}

import scala.concurrent.Future
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext.Implicits.global

class GoogleGroupsTest extends FlatSpec with Matchers with ScalaFutures with ParallelTestExecution with AsyncAssertions {

  val identity = UserIdentity("sub","test.com","firstName","lastName",1234,None)
  val adminIdentity = AdminIdentity(identity)
  val requiredGroups = Set("validGroup")

  behavior of "GoogleGroups$Test"

  "isUserAdmin" should "return an identity if the user is in the required groups" in {
    def validUserGroups(identity: UserIdentity): Future[Set[String]] = Future.successful(Set("validGroup"))

    whenReady(Admin.checkAdminGroups(validUserGroups, identity, requiredGroups)) { result =>
      result should be(\/-(adminIdentity))
    }
  }

  it should "handle errors" in {
    def invalidUserGroups(identity: UserIdentity): Future[Set[String]] = Future.successful(Set("invalidGroup"))

    whenReady(Admin.checkAdminGroups(invalidUserGroups, identity, requiredGroups)) { result =>
      result should be(-\/(GroupsValidationFailed()))
    }

    def futureFailed(identity: UserIdentity): Future[Set[String]] = Future.failed(new Throwable)

    whenReady(Admin.checkAdminGroups(futureFailed, identity, requiredGroups)) { result =>
      result should be(-\/(GroupsValidationFailed()))
    }
  }

}
