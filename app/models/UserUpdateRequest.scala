package models

import play.api.libs.json.{Json, JsValue}

case class UserUpdateRequest(
                              email: String,
                              username: Option[String] = None,
                              firstName: Option[String] = None,
                              lastName: Option[String] = None,
                              receiveGnmMarketing: Option[Boolean] = None,
                              receive3rdPartyMarketing: Option[Boolean] = None)

object UserUpdateRequest {
  def convertToJson(userData: UserUpdateRequest): JsValue = {
    Json.obj(
      "email" -> userData.email,
      "username" -> userData.username,
      "firstName" -> userData.firstName,
      "lastName" -> userData.lastName,
      "receiveGnmMarketing" -> userData.receiveGnmMarketing,
      "receive3rdPartyMarketing" -> userData.receive3rdPartyMarketing
    )
  }
}
