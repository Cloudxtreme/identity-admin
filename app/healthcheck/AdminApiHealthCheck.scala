package healthcheck

import akka.actor.ActorSystem
import akka.agent.Agent
import monitoring.CloudWatch
import services.AdminApi
import util.Logging
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

class AdminApiHealthCheck(adminApi: AdminApi, actorSystem: ActorSystem) extends Logging {

  private val healthy = Agent[Boolean](false)(actorSystem.dispatcher)

  def get = healthy.get()

  private[healthcheck] def triggerUpdate() {
    logger.debug("Updating Admin API HealthCheck")

    adminApi.authHealthCheck.map {
      case Right(_) =>
        logger.debug("Admin API Auth service is available")
        CloudWatch.publishMetric("AdminApiHealth", 1)
        healthy send { _ => true }
      case Left(err) =>
        logger.error("Admin API Auth service is unavailable: {}", err.details)
        CloudWatch.publishMetric("AdminApiHealth", 0)
        healthy send { _ => false }
    }
  }

  def start() {
    logger.info("Admin API HealthCheck agent started")
    triggerUpdate() // trigger immediately

    actorSystem.scheduler.schedule(10.seconds, 10.seconds) {
      triggerUpdate()
    }
  }
}
