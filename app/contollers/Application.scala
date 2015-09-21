package contollers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def healthCheck = Action {
    Ok("200 OK")
  }
}
