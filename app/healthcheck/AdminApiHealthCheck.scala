package healthcheck

import akka.actor.ActorSystem
import akka.agent.Agent
import services.AdminApi
import javax.inject.Inject
import util.Logging
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import scala.language.postfixOps

class AdminApiHealthCheck @Inject() (adminApi: AdminApi, actorSystem: ActorSystem) extends Logging {

  private val healthy = Agent[Boolean](false)(actorSystem.dispatcher)

  def get = healthy.get()

  private[healthcheck] def triggerUpdate() {
    logger.debug("Updating Admin API HealthCheck")

    adminApi.authHealthCheck.map {
      case Right(_) =>
        logger.debug("Admin API Auth service is available")
        healthy send { _ => true }
      case Left(err) =>
        logger.error("Admin API Auth service is unavailable: {}", err.details)
        healthy send { _ => false }
    }
  }

  private def checkAdminApiHealthy(): Unit = {
    logger.debug("Checking if Admin API HealthCheck agent is healthy")
    if (!get) {
      SNS.notifyAdminApiUnhealthy()
    }
  }

  def start() {
    logger.info("Admin API HealthCheck agent started")
    // trigger immediately
    triggerUpdate()

    // trigger every minute
    actorSystem.scheduler.schedule(1 minute, 10 minutes) {
      triggerUpdate()
    }

    actorSystem.scheduler.schedule(2 minutes, 6 hours) {
      checkAdminApiHealthy()
    }
  }

}
