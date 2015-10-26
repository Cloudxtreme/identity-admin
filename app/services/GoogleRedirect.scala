package services

import com.gu.googleauth.{GoogleAuth, UserIdentity, GoogleAuthConfig}
import play.api.Application
import play.api.mvc.Results._
import play.api.mvc.{Result, RequestHeader}

import scala.concurrent.{Future, ExecutionContext}

object GoogleRedirect {

  def redirectToGoogle(config: GoogleAuthConfig, antiForgeryToken: String)
                      (implicit request: RequestHeader, context: ExecutionContext, application: Application): Future[Result] = {
    val userIdentity = UserIdentity.fromRequest(request)
    val queryString: Map[String, Seq[String]] = Map(
      "client_id" -> Seq(config.clientId),
      "response_type" -> Seq("code"),
      "scope" -> Seq("openid email profile"),
      "redirect_uri" -> Seq(config.redirectUrl),
      "prompt" -> Seq("login"),
      "state" -> Seq(antiForgeryToken)) ++
      config.domain.map(domain => "hd" -> Seq(domain)) ++
      userIdentity.map(_.email).map("login_hint" -> Seq(_))

    GoogleAuth.discoveryDocument.map(dd => Redirect(s"${dd.authorization_endpoint}", queryString))
  }
}
