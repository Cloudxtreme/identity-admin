package models

import play.api.data.Form
import play.api.data.Forms._

object Forms {

  case class UserData(
                       userId: String,
                       emailAddress: String,
                       firstName: String,
                       lastName: String,
                       username: String,
                       receiveGnmMarketing: Boolean,
                       receive3rdPartyMarketing: Boolean
                     )

  val userForm = Form(
    mapping(
      "userId" -> text,
      "emailAddress" -> text,
      "firstName" -> text,
      "lastName" -> text,
      "username" -> text,
      "receiveGnmMarketing" -> boolean,
      "receive3rdPartyMarketing" -> boolean
    )(UserData.apply)(UserData.unapply)
  )
}
