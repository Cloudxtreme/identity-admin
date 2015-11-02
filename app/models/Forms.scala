package models

import org.joda.time.DateTime
import play.api.data.{Mapping, Form}
import play.api.data.Forms._

object Forms {

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

//  Experimentation with validation
  private val alphaText: Mapping[String] = text.verifying("First name must be characters only." , name => name.matches("[A-z]+"))
  private val username: Mapping[String] = text.verifying("Invalid display name, ", name => name.matches("[A-z0-9]+") && name.length > 5 && name.length < 21)
  private val email1: Mapping[String] = text.verifying("valid", name => name.length > 4)

  val userForm = Form(
    mapping(
      "id" -> text,
      "email" -> email,
      "displayName" -> optional(text),
      "username" -> text,
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
      "lastActivityDate" -> optional(jodaDate),
      "lastActivityIp" -> optional(text),
      "registrationDate" -> optional(jodaDate),
      "registrationIp" -> optional(text),
      "status" -> mapping(
        "receive3rdPartyMarketing" -> optional(boolean),
        "receiveGnmMarketing" -> optional(boolean),
        "userEmailValidated" -> optional(boolean)
      )(UserStatus.apply)(UserStatus.unapply),
      "groups" -> seq(
        mapping(
          "name" -> text,
          "joinDate" -> optional(jodaDate)
        )(UserGroup.apply)(UserGroup.unapply)
      )
    )(UserForm.apply)(UserForm.unapply)
  )
}
