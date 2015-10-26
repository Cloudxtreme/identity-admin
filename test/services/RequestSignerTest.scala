package services

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import play.api.http.HeaderNames
import play.api.libs.ws.WS
import play.api.test.FakeApplication
import org.mockito.Mockito._

class RequestSignerTest extends WordSpec with Matchers with MockitoSugar {

  class TestSigner extends RequestSigner {
    override def secret: String = "secret"
  }

  val signer = spy(new TestSigner)

  implicit val app = FakeApplication()

  "sign" should {
    "set a Date header" in {
      val request = WS.url("http://localhost/path/to/resource").withQueryString("param" -> "value")

      val result = signer.sign(request)

      val header = result.headers.get(HeaderNames.DATE)
      header shouldNot equal(None)
    }

    "set an Authorization header" in {
      val request = WS.url("http://localhost/path/to/resource").withQueryString("param" -> "value")
      val date = "2015-10-23T16:54:00Z"
      doReturn(date).when(signer).getDateHeaderValue
      val result = signer.sign(request)

      val header = result.headers.get(HeaderNames.AUTHORIZATION)
      header shouldNot equal(None)
      val token = "HMAC\\s(.+)".r.findAllIn(header.get.head).matchData map {
        m => m.group(1)
      }
      val expected = signer.sign(date, "/path/to/resource?param=value")
      token.toSeq.headOption shouldEqual Some(expected)
    }
  }
}
