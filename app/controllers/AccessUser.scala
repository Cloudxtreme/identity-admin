package controllers

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import models.{UserStatus, Address, PersonalDetails, User}
import play.api.libs.concurrent.Execution.Implicits._

class AccessUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  def getUser(searchQuery: String, userId: String) = AuthAction.async {
    val x = "http://localhost:9500/v1/user:10000001"
    val y = Address(None, None, None, None, None, None)
    WS.url(x).get().map {
      response =>
        Ok(views.html.editUser(Messages("editUser.title"), Some(searchQuery),User(
        "10000001",
        "email@email.com",
        None,
        None,
        None,
        PersonalDetails(
          None,
          None,
          None,
          None,
          None
        ),
        y,
        y,
        None,
        None,
        None,
        None,
        UserStatus(None,None,None)
        )))

//          Json.parse(response.body).as[User]))
    }
  }
}
