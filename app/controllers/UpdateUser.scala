package controllers

import play.api.mvc.Action
import javax.inject.Inject
import models.Forms._
import models.User
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.Controller
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

class UpdateUser @Inject() (adminApi: AdminApi) extends Controller with AuthActions with Logging {

  val blankUser = User("","")

  def save = Action.async { request =>
      val userId = request.body.asFormUrlEncoded.get("userId").head
      val searchQuery= request.body.asFormUrlEncoded.get("searchQuery").head
      userForm.bindFromRequest()(request).fold(
        formWithErrors => {
          println(formWithErrors)
          adminApi.getFullUser(userId).map {
            case Right(user) =>
              BadRequest(views.html.editUser(Messages("editUser.title"), Some(searchQuery), user,formWithErrors, Some(Messages("editUser.invalidSubmission")), None))
            case Left(error) =>
              BadRequest(views.html.editUser(Messages("editUser.title"), Some(searchQuery), blankUser,formWithErrors, Some(error.toString), None))
          }
          },
        userData => {
          println(userData)
//          adminApi.updateUserData(userData)
          Future.successful(Redirect(routes.AccessUser.getUser(searchQuery, userId)).flashing("success" -> "User has been updated"))
        }
      )

    }

}
