package filters

import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object StrictTransportSecurityHeaderFilter extends Filter {

  private val OneYearInSeconds = 31536000
  private val Header = "Strict-Transport-Security" -> s"max-age=$OneYearInSeconds; preload"

  override def apply(nextFilter: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    nextFilter(request).map(_.withHeaders(Header))
  }
}