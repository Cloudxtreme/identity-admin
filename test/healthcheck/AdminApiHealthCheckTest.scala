package healthcheck

import akka.actor.ActorSystem
import org.scalatest.concurrent.Eventually
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FunSuite, Matchers}
import services.{CustomError, AdminApi}

import scala.concurrent.Future


class AdminApiHealthCheckTest extends FunSuite with MockitoSugar with Matchers with Eventually {

  val actorSystem = ActorSystem("test")

  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(Span(2, Seconds)), interval = scaled(Span(5, Millis)))

  test("reports as unhealthy when admin api auth is unavailable") {
    val adminApi = mock[AdminApi]
    val sns = mock[SNS]
    when(adminApi.authHealthCheck)
      .thenReturn(Future.successful(Right("OK")))
      .thenReturn(Future.successful(Left(CustomError("Service unavailable","401"))))
    val hc = new AdminApiHealthCheck(sns, adminApi, actorSystem)

    // if no successful future is returned, the agent will never become healthy
    hc.triggerUpdate()
    eventually {
      hc.get shouldEqual true
    }

    // if no unsuccessful future is returned, the agent will never become unhealthy
    hc.triggerUpdate()
    eventually {
      hc.get shouldEqual false
    }
  }

}
