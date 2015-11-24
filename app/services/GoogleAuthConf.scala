package services

import com.gu.googleauth.GoogleAuthConfig
import play.api.Play._
import play.api.Play.current

object GoogleAuthConf {

  val clientId = current.configuration.getString("identity-admin.google.clientId").get
  val clientSecret = current.configuration.getString("identity-admin.google.clientSecret").get
  val redirectUrl = current.configuration.getString("identity-admin.google.authorisationCallback").get
  val impersonatedUser = current.configuration.getString("identity-admin.google.impersonatedUser").get
  val sessionMaxAge = current.configuration.getString("play.http.session.maxAge").get.toLong
  val userAdminGroup = current.configuration.getString("identity-admin.groups.useradmin").get

  val googleAuthConfig =
    GoogleAuthConfig(
      clientId = clientId,
      clientSecret = clientSecret,
      redirectUrl = redirectUrl,
      domain = Some("guardian.co.uk"),
      prompt = Some("login")
    )
}
