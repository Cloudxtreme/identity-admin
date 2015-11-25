package services

import javax.inject._
import auth.AccessForbidden
import controllers.routes
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent.Future


class CustomErrorHandler @Inject() (
                                   env: Environment,
                                   config: Configuration,
                                   sourceMapper: OptionalSourceMapper,
                                   router: Provider[Router]
                                   ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router){

  override def onForbidden(request: RequestHeader, message: String) = {
        Future.successful(Redirect(routes.Login.login(Some(AccessForbidden()))))
  }
}
