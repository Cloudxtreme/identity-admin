package controllers

import javax.inject.Inject
import auth.CSRF._
import com.google.inject.ImplementedBy
import models.Forms._
import models.SearchResponse
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc.{Request, Results, Result, Controller}
import services.AdminApi
import util.Logging
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

@ImplementedBy(classOf[Search])
trait SearchAction extends Results {
  val adminApi: AdminApi

  def doSearch(searchQuery: String)(implicit request: Request[Any]): Future[Result] = {
    adminApi.getUsers(searchQuery) map {
      case Right(searchResult) =>
        if(searchResult.total == 1 && !searchResult.results.isEmpty) {
          Redirect(routes.AccessUser.getUser(searchResult.results.head.id)).flashing(request.flash)
        } else {
          Ok(
            views.html.searchResults(
              Messages("searchResults.title"),
              Some(searchQuery),
              searchResult,
              request.flash.get("message"),
              request.flash.get("error")
            )
          )
        }
      case Left(error) =>
        Ok(
          views.html.searchResults(
            Messages("searchResults.title"),
            Some(searchQuery),
            SearchResponse(0, false),
            request.flash.get("message"),
            Some(error.toString)
          )
        )
    }
  }
}

class Search @Inject() (val adminApi: AdminApi) extends Controller with SearchAction with AuthActions with Logging {

  def search = AuthAction.async { implicit request =>
    searchForm.bindFromRequest.fold(
      errorForm => {
        Future(Redirect(routes.Application.index()).flashing("error" -> Messages("search.failed")))
      },
      userData => {
        doSearch(userData.query)
      }
    )
  }
}
