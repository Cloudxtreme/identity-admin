package auth

import com.gu.googleauth.UserIdentity
import config.MainConfig
import play.api.mvc.Controller
import play.api.mvc.Security.AuthenticatedRequest
import services.{RequestSignerWithSecret, AdminApi}

trait AdminApiProvider { self: Controller =>
  implicit def adminApi(implicit request: AuthenticatedRequest[_, UserIdentity]): AdminApi = {
    val conf = new MainConfig
    val requestSigner = new RequestSignerWithSecret
    new AdminApi(conf, requestSigner)
  }
}
