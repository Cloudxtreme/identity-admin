package config

import com.google.inject.ImplementedBy
import play.api.Play._

@ImplementedBy(classOf[MainConfig])
trait Config {
  val baseUrl: String
  val baseRootUrl: String
  val errorEmail: String
}

class MainConfig extends Config {
  val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.adminApi.baseUrl"))

  val baseRootUrl = current.configuration.getString("identity-admin.adminApi.baseRootUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.adminApi.baseRootUrl"))

  val errorEmail = current.configuration.getString("identity-admin.email.error")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.email.error"))
}