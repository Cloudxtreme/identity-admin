package auth

import com.gu.googleauth.UserIdentity
import config.MainConfig
import play.api.mvc.Controller
import play.api.mvc.Security.AuthenticatedRequest
import services.AdminApi

trait AdminApiProvider { self: Controller =>
  implicit def adminApi(implicit request: AuthenticatedRequest[_, UserIdentity]): AdminApi = {
    new AdminApi
  }
}
