package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class AuthorisationTest extends FlatSpec with Matchers {

  val required = Set("2FA_enforce","useradmin")

  it should "determine if user is in correct groups" in {
    Authorisation.isAuthorised(required, Set("2FA_enforce","useradmin")) should be (true)
  }

  it should "determine order of groups in the authorised set has no effect" in {
    Authorisation.isAuthorised(required, Set("useradmin","2FA_enforce")) should be (true)
  }

  it should "determine additional groups in authorised set has no effect on authorisation" in {
    Authorisation.isAuthorised(required, Set("2FA_enforce","useradmin","testgroup")) should be (true)
  }

  it should "determine the absence of the 'useradmin' group prevents authorisation" in {
    Authorisation.isAuthorised(required, Set("2FA_enforce")) should be (false)
  }

  it should "determine the absence of the '2FA_enforce' group prevents authorisation" in {
    Authorisation.isAuthorised(required, Set("2FA_enforce")) should be (false)
  }
}
