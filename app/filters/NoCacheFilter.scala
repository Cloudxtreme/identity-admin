package filters

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

object NoCacheFilter extends Filter {

  private val Header = "cache-control" -> "no-cache, no-store, max-age=0, must-revalidate"

  override def apply(nextFilter: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    nextFilter(request).map(_.withHeaders(Header))
  }
}

