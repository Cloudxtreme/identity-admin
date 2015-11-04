package large.util

import large.util.Config.baseUrl
import java.util.concurrent.TimeUnit
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{By, WebDriver}
import org.scalatest.selenium.WebBrowser

import scala.util.Try

trait Util { this: WebBrowser => implicit val driver: WebDriver

  def resetDriver() = {
    go.to(Config.baseUrl)
    driver.manage().deleteAllCookies()
    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS)
  }

  private val defaultTimeOut = 90

  protected def pageHasText(text: String, timeoutSecs: Int=defaultTimeOut): Boolean = {
    val pred = ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text)
    Try {
      new WebDriverWait(driver, timeoutSecs).until(pred)
    }.isSuccess
  }

  protected def pageHasElement(q: Query, timeoutSecs: Int=defaultTimeOut): Boolean = {
    val pred = ExpectedConditions.visibilityOfElementLocated(q.by)
    Try {
      new WebDriverWait(driver, timeoutSecs).until(pred)
    }.isSuccess
  }

  def hasSessionCookie: Boolean = {
    (Try { cookie("PLAY_SESSION") }.isSuccess
       && (cookie("PLAY_SESSION").domain == baseUrl.substring("https://".length)))
  }
}
