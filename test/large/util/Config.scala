package large.util

import com.typesafe.config.ConfigFactory
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import scala.util.Try

object Config {
  private val conf = ConfigFactory.load()

  private val targetHosts = Map(
    "DEV" -> "https://useradmin.thegulocal.com",
    "CODE" -> "https://useradmin.code.dev-gutools.co.uk",
    "PROD" -> "https://useradmin.gutools.co.uk"
  )
  val stage = conf.getString("identity-admin.stage")

  val baseUrl = targetHosts(stage)

  val googleAccountsUrl = "https://accounts.google.com/ServiceLogin"

  /**
   * We cannot use Sauce Labs because we must be inside GNM network, hence
   * we must use a headless webdriver to be able to execute in TeamCity CI.
   */
  lazy val driver: WebDriver = {
    Try { conf.getString("useHeadlessWebDriver") }.toOption.map { _ =>
      new HtmlUnitDriver()
    }.getOrElse {
      new ChromeDriver()
    }
  }
}
