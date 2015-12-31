package services

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.google.inject.ImplementedBy
import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime
import play.api.Play._
import play.api.http.HeaderNames
import play.api.libs.ws.WSRequest
import util.{Formats, Logging}

class RequestSignerWithSecret extends RequestSigner {
  val secret = current.configuration.getString("identity-admin.adminApi.secret").get
}

@ImplementedBy(classOf[RequestSignerWithSecret])
trait RequestSigner extends Logging {

  def secret: String

  private val ALGORITHM = "HmacSHA256"
  private def hmacHeaderValue(hmacToken: String) = s"HMAC $hmacToken"

  def sign(request: WSRequest): WSRequest = {
    val path = getPath(request)
    val dateHeaderValue = getDateHeaderValue
    val hmacToken = sign(dateHeaderValue, path)
    logger.info(s"path: $path, date: $dateHeaderValue, hmac: $hmacToken")
    request.withHeaders(HeaderNames.DATE -> dateHeaderValue, HeaderNames.AUTHORIZATION -> hmacHeaderValue(hmacToken))
  }

  private[services] def getPath(request: WSRequest): String = {
    val uri = request.uri
    val queryString = uri.getRawQuery
    if(queryString == null || queryString.isEmpty) uri.getPath else s"${uri.getPath}?" + queryString.replace("+", "%20")
  }

  private[services] def getDateHeaderValue: String = {
    val now = DateTime.now()
    Formats.toHttpDateTimeString(now)
  }

  private[services] def sign(date: String, path: String): String = {
    val input = List[String](date, path)
    val toSign = input.mkString("\n")
    calculateHMAC(toSign)
  }

  private def calculateHMAC(toEncode: String): String = {
    val signingKey = new SecretKeySpec(secret.getBytes, ALGORITHM)
    val mac = Mac.getInstance(ALGORITHM)
    mac.init(signingKey)
    val rawHmac = mac.doFinal(toEncode.getBytes)
    new String(Base64.encodeBase64(rawHmac))
  }

}
