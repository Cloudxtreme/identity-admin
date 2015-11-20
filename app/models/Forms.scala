package models

import org.joda.time.DateTime
import play.api.data.{Mapping, Form}
import play.api.data.Forms._

object Forms {

  val DateTimeFormat: String = "dd-MM-YYYY, HH:mm:ss"

  case class IdForm(id: String)

  case class UserForm(
    id: String,
    email: String,
    displayName: Option[String] = None,
    username: String,
    vanityUrl: Option[String] = None,
    personalDetails: PersonalDetails = PersonalDetails(),
    deliveryAddress: Address = Address(),
    billingAddress: Address = Address(),
    lastActivityDate: Option[DateTime] = None,
    lastActivityIp: Option[String] = None,
    registrationDate: Option[DateTime] = None,
    registrationIp: Option[String] = None,
    registrationType: Option[String] = None,
    status: UserStatus = UserStatus(),
    groups: Seq[UserGroup] = Nil){

    val convertToUserUpdateRequest = UserUpdateRequest(
      this.email,
      this.username,
      this.personalDetails.firstName,
      this.personalDetails.lastName,
      this.status.receiveGnmMarketing,
      this.status.receive3rdPartyMarketing
    )
  }

  private val username: Mapping[String] = text.verifying(
    "error.username", name => name.matches("[A-z0-9]+") && name.length > 5 && name.length < 21
  )

  val userForm = Form(
    mapping(
      "id" -> text,
      "email" -> email,
      "displayName" -> optional(text),
      "username" -> username,
      "vanityUrl" -> optional(text),
      "personalDetails" -> mapping(
        "firstName" -> optional(text),
        "lastName" -> optional(text),
        "gender" -> optional(text),
        "dateOfBirth" -> optional(jodaLocalDate),
        "location" -> optional(text)
      )(PersonalDetails.apply)(PersonalDetails.unapply),
      "deliveryAddress" -> mapping(
        "addressLine1" -> optional(text),
        "addressLine2" -> optional(text),
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "country" -> optional(text),
        "postcode" -> optional(text)
      )(Address.apply)(Address.unapply),
      "billingAddress" -> mapping(
        "addressLine1" -> optional(text),
        "addressLine2" -> optional(text),
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "country" -> optional(text),
        "postcode" -> optional(text)
      )(Address.apply)(Address.unapply),
      "lastActivityDate" -> optional(jodaDate(DateTimeFormat)),
      "lastActivityIp" -> optional(text),
      "registrationDate" -> optional(jodaDate(DateTimeFormat)),
      "registrationIp" -> optional(text),
      "registrationType" -> optional(text),
      "status" -> mapping(
        "receive3rdPartyMarketing" -> optional(boolean),
        "receiveGnmMarketing" -> optional(boolean),
        "userEmailValidated" -> optional(boolean)
      )(UserStatus.apply)(UserStatus.unapply),
      "groups" -> seq(
        mapping(
          "name" -> text,
          "joinDate" -> optional(jodaDate(DateTimeFormat))
        )(UserGroup.apply)(UserGroup.unapply)
      )
    )(UserForm.apply)(UserForm.unapply)
  )

  def createForm(user: User): Form[UserForm] = {
    Forms.userForm.fill(UserForm(
      user.id,
      user.email,
      user.displayName,
      user.username.getOrElse(""),
      user.vanityUrl,
      user.personalDetails,
      user.deliveryAddress,
      user.billingAddress,
      user.lastActivityDate,
      user.lastActivityIp,
      user.registrationDate,
      user.registrationIp,
      user.registrationType,
      user.status,
      user.groups
    ))
  }

  val idForm = Form(
    mapping("id" -> text)(IdForm.apply)(IdForm.unapply)
  )
}
