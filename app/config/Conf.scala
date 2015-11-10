package config

import play.api.Play._

object Conf {

  val errorEmail = current.configuration.getString("identity-admin.email.error").get

}
