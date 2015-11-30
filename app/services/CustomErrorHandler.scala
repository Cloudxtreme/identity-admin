package services

import javax.inject._
import auth.AccessForbidden
import controllers.routes
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent.Future


class CustomErrorHandler @Inject() (
                                   env: Environment,
                                   config: Configuration,
                                   sourceMapper: OptionalSourceMapper,
                                   router: Provider[Router]
                                   ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router){

  //Client Errors 4xx series
  override def onForbidden(request: RequestHeader, message: String):Future[Result] = {
        Future.successful(loginRedirect)
  }

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
        Future.successful(Redirect(routes.Application.index()).flashing("error" -> "404 resource not found."))
  }

  override def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(loginRedirect)
  }

  private val loginRedirect = Redirect(routes.Login.login(Some(AccessForbidden())))
}
