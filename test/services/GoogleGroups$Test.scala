package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Seconds, Span, Millis}

class GoogleGroups$Test extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {
  /*
  "userGroups" should "print out details" in {
    val f = GoogleGroups.userGroups("mark.butler@guardian.co.uk")

    whenReady(f) {
      res => res should be(Right)
    }
  }
  */

  it should "determine if user is in correct groups" in {
    val required = Set("2FA_enforce","useradmin")
    GoogleGroups.isAuthorised(required, Set("2FA_enforce","useradmin")) should be (true)
    GoogleGroups.isAuthorised(required, Set("2FA_enforce","useradmin","testgroup")) should be (true)
    GoogleGroups.isAuthorised(required, Set("2FA_enforce")) should be (false)
  }
}
