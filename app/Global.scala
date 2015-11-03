import play.api._
import play.api.Play.current
import healthcheck.AdminApiHealthCheck
import services.{RequestSignerWithSecret, AdminApi}
import play.api.libs.concurrent.Akka.system
import util.Logging

object Global extends GlobalSettings with Logging {

  override def onStart(app: Application) {
    if (Play.mode == Mode.Prod) {
      logger.info("Starting Admin API HealthCheck agent...")
      val requestSigner = new RequestSignerWithSecret
      val adminApi = new AdminApi(requestSigner)
      new AdminApiHealthCheck(adminApi, system).start()
    }
  }
}