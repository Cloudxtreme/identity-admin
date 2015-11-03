package models

import play.api.libs.json.Json

case class UserUpdateRequest(
                              email: String,
                              username: String,
                              firstName: Option[String] = None,
                              lastName: Option[String] = None,
                              receiveGnmMarketing: Option[Boolean] = None,
                              receive3rdPartyMarketing: Option[Boolean] = None)

object UserUpdateRequest {
  implicit val format = Json.format[UserUpdateRequest]
}
