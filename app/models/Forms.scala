package models

import play.api.data.Form
import play.api.data.Forms._

object Forms {

  case class UserUpdateRequest(
                                email: String,
                                username: String,
                                firstName: Option[String] = None,
                                lastName: Option[String] = None,
                                receiveGnmMarketing: Option[Boolean] = None,
                                receive3rdPartyMarketing: Option[Boolean] = None)

  val userForm = Form(
    mapping(
      "email" -> text,
      "username" -> text,
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "receiveGnmMarketing" -> optional(boolean),
      "receive3rdPartyMarketing" -> optional(boolean)
    )(UserUpdateRequest.apply)(UserUpdateRequest.unapply)
  )
}
