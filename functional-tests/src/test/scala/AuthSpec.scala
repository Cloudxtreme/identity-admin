package test

import test.util.{Util, Config}
import test.pages.Homepage

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}

class AuthSpec extends FeatureSpec
  with WebBrowser with Util
  with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll  {

  implicit lazy val driver: WebDriver = Config.driver

  before { resetDriver() }

  override def afterAll(): Unit = { quit() }

  feature("Identity Admin Authentication") {
    info(s"in ${Config.stage} environment: ${Config.baseUrl}")

    scenario("User tries to log in to ID Admin.") {
      Given("the user has not been previously authenticated")
      assert(!hasSessionCookie)

      When("the user opens ID Admin homepage")
      go to new Homepage

      Then("Google OpenID Connect authentication process should start")
      assert(pageHasText("Sign in with your Google Account"))

      And("we should be on Google domain")
      assert(currentUrl.startsWith(Config.googleAccountsUrl))
    }

    scenario("Un-authenticated user tries to search ID database.") {
      Given("the user has not been previously authenticated")
      assert(!hasSessionCookie)

      When("attempting to search ID database")
      go to s"${Config.baseUrl}/search?searchQuery=test"

      Then("Google OpenID Connect authentication process should start")
      assert(pageHasText("Sign in with your Google Account"))

      And("we should be on Google domain")
      assert(currentUrl.startsWith(Config.googleAccountsUrl))
    }

    scenario("Un-authenticated user tries to delete a record in ID database.") {
      Given("the user has not been previously authenticated")
      assert(!hasSessionCookie)

      When("attempting to delete a record in ID database")
      go to s"${Config.baseUrl}/delete?searchQuery=98f36ffbc%40gu.com&userId=11111111"

      Then("Google OpenID Connect authentication process should start")
      assert(pageHasText("Sign in with your Google Account"))

      And("we should be on Google domain")
      assert(currentUrl.startsWith(Config.googleAccountsUrl))
    }
  }
}

