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
    logger.debug("Updating healthcheck")

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
    if (!get) {
      SNS.notifyAdminApiUnhealthy()
    }
  }

  def start() {
    // trigger immediately
    triggerUpdate()

    // trigger every minute
    actorSystem.scheduler.schedule(1 minute, 10 minute) {
      triggerUpdate()
    }

    actorSystem.scheduler.schedule(2 minute, 6 hour) {
      checkAdminApiHealthy()
    }
  }

}
