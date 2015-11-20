import controllers.routes
import filters.LogRequestsFilter
import play.api._
import play.api.Play.current
import healthcheck.{SNS, AdminApiHealthCheck}
import play.api.mvc.{WithFilters, Result, RequestHeader}
import play.api.mvc.Results.Redirect
import services.AdminApi
import play.api.libs.concurrent.Akka.system
import util.Logging

import scala.concurrent.Future

object Global extends WithFilters(LogRequestsFilter) with GlobalSettings with Logging {

  override def onStart(app: Application) {
    if (Play.mode == Mode.Prod) {
      logger.info("Starting Admin API HealthCheck agent...")
      val adminApi = app.injector.instanceOf(classOf[AdminApi])
      val sns = app.injector.instanceOf(classOf[SNS])
      new AdminApiHealthCheck(sns, adminApi, system).start()
    }
    else {
      println("Not starting Admin API HealthCheck agent in DEV mode")
    }
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful(Redirect(routes.Application.index()))
  }
}