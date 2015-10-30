package models

import play.api.data.{Mapping, Form}
import play.api.data.Forms._

object Forms {

  case class UserForm(
    email: String,
    username: String,
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    receiveGnmMarketing: Option[Boolean] = None,
    receive3rdPartyMarketing: Option[Boolean] = None) {

    val convertToUserUpdateRequest = UserUpdateRequest(
      this.email,
      this.username,
      this.firstName,
      this.lastName,
      this.receiveGnmMarketing,
      this.receive3rdPartyMarketing
    )
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
    )(UserForm.apply)(UserForm.unapply)
  )
}
