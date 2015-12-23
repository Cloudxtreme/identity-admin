package auth

import com.gu.googleauth.UserIdentity
import play.api.mvc.Controller
import play.api.mvc.Security.AuthenticatedRequest
import services.AdminApi

// An admin api will be provided only if the request is an AuthenticatedRequest, and won't compile otherwise
// It ensures that no access to the api will be accidentally provided if a request is unauthenticated
trait AdminApiProvider { self: Controller => // `self: Controller` ensures the trait can only be mixed in controllers
  def adminApi(implicit request: AuthenticatedRequest[_, UserIdentity]): AdminApi = {
    new AdminApi
  }
}
