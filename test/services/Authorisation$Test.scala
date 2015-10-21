package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class Authorisation$Test extends FlatSpec with Matchers {

  it should "determine if user is in correct groups" in {
    val required = Set("2FA_enforce","useradmin")
    Authorisation.isAuthorised(required, Set("2FA_enforce","useradmin")) should be (true)
    Authorisation.isAuthorised(required, Set("useradmin","2FA_enforce")) should be (true)
    Authorisation.isAuthorised(required, Set("2FA_enforce","useradmin","testgroup")) should be (true)
    Authorisation.isAuthorised(required, Set("2FA_enforce")) should be (false)
  }
}
