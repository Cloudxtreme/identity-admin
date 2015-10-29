package models

import play.api.data.{Mapping, Form}
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}

object Forms {

  case class UserUpdateRequest(
                                email: String,
                                username: String,
                                firstName: Option[String] = None,
                                lastName: Option[String] = None,
                                receiveGnmMarketing: Option[Boolean] = None,
                                receive3rdPartyMarketing: Option[Boolean] = None)

  object UserUpdateRequest {
    def convertUserUpdateRequestToJson(userData: UserUpdateRequest): JsValue = {
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


//  Experimentation with validation
  private val alphaText: Mapping[String] = text.verifying("First name must be characters only." , name => name.matches("[A-z]+"))
  private val username: Mapping[String] = text.verifying("Invalid display name, ", name => name.matches("[A-z0-9]+") && name.length > 5 && name.length < 21)
  private val email1: Mapping[String] = text.verifying("valid", name => name.length > 4)

  val userForm = Form(
    mapping(
      "email" -> email,
      "username" -> text,
      "firstName" -> optional(alphaText),
      "lastName" -> optional(alphaText),
      "receiveGnmMarketing" -> optional(boolean),
      "receive3rdPartyMarketing" -> optional(boolean)
    )(UserUpdateRequest.apply)(UserUpdateRequest.unapply)
  )
}
