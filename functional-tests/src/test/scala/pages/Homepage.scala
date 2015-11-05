package test.pages

import test.util.{Config, Util}
import test.util.Util
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{Element, click}
import org.scalatest.selenium.{WebBrowser, Page}

class Homepage(implicit val driver: WebDriver) extends Page
  with WebBrowser with Util {
  val url = Config.baseUrl
}
