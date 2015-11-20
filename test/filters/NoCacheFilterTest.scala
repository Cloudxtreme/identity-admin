package filters

import org.scalatest.{FunSuite, Matchers}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.test.FakeRequest

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class NoCacheFilterTest extends FunSuite with Matchers {

  test("filter should add the correct no cache headers") {
    val request = FakeRequest()
    def action(req: RequestHeader): Future[Result] = Future.successful(Ok)

    val result = NoCacheFilter(action _)(request)

    Await.result(result, 1.second).header.headers("cache-control") should equal("no-cache, no-store, max-age=0, must-revalidate")
  }

}

