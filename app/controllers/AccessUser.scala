package controllers

import javax.inject.Inject
import models.UserMock
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import models.User
import play.api.libs.concurrent.Execution.Implicits._

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def getUser(searchQuery: String, userId: String) = AuthAction.async {
    val x = "http://localhost:9500/v1/user:10000001"
    WS.url(x).get().map {
      response => Json.parse(response.body).as[User]
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery), UserMock.mockData()))
    }
  }
}
