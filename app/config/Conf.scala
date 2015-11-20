package config

import com.google.inject.ImplementedBy
import play.api.Play._

@ImplementedBy(classOf[MainConfig])
trait Config {
  val baseUrl: String
  val baseRootUrl: String
  val errorEmail: String
  val baseProfileUrl: String
  val avatarUrl: String
}

class MainConfig extends Config {
  val baseUrl = current.configuration.getString("identity-admin.adminApi.baseUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.adminApi.baseUrl"))

  val baseRootUrl = current.configuration.getString("identity-admin.adminApi.baseRootUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.adminApi.baseRootUrl"))

  val errorEmail = current.configuration.getString("identity-admin.email.error")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.email.error"))

  val baseProfileUrl = current.configuration.getString("identity-admin.editUser.baseProfileUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.editUser.baseProfileUrl"))

  val avatarUrl = current.configuration.getString("identity-admin.avatar.baseUrl")
    .getOrElse(throw new IllegalStateException("Missing configuration: identity-admin.avatar.baseUrl"))
}
