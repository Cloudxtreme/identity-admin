package models

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json.Json
import play.api.mvc.{Results, Result}
import scala.language.implicitConversions
import repositories.User


case class PersonalDetails(firstName: Option[String] = None,
                           lastName: Option[String] = None,
                           gender: Option[String] = None,
                           dateOfBirth: Option[LocalDate] = None,
                           location: Option[String] = None)

object PersonalDetails {
  implicit val format = Json.format[PersonalDetails]
}

case class Address(addressLine1: Option[String] = None,
                   addressLine2: Option[String] = None,
                   addressLine3: Option[String] = None,
                   addressLine4: Option[String] = None,
                   country: Option[String] = None,
                   postcode: Option[String] = None)

object Address {
  implicit val format = Json.format[Address]
}

case class UserStatus(receive3rdPartyMarketing: Option[Boolean] = None,
                      receiveGnmMarketing: Option[Boolean] = None,
                      userEmailValidated: Option[Boolean] = None)

object UserStatus {
  implicit val format = Json.format[UserStatus]
}

case class UserGroup(name: String,
                     joinDate: Option[DateTime])

object UserGroup {
  implicit val format = Json.format[UserGroup]
}

case class LastActiveLocation(countryCode: Option[String] = None,
                              cityCode : Option[String] = None)

object LastActiveLocation {
  implicit val format = Json.format[LastActiveLocation]
}

case class UserResponse(id: String,
                        email: String,
                        displayName: Option[String] = None,
                        username: Option[String] = None,
                        vanityUrl: Option[String] = None,
                        personalDetails: PersonalDetails = PersonalDetails(),
                        deliveryAddress: Address = Address(),
                        billingAddress: Address = Address(),
                        lastActivityDate: Option[DateTime] = None,
                        lastActivityIp: Option[String] = None,
                        registrationDate: Option[DateTime] = None,
                        registrationIp: Option[String] = None,
                        status: UserStatus = UserStatus(),
                        groups: Seq[UserGroup] = Nil)

object UserResponse {
  implicit val format = Json.format[UserResponse]

  implicit def userResponseToResult(userResponse: UserResponse): Result =
    Results.Ok(Json.toJson(userResponse))

  def fromUser(user: User): UserResponse =
    UserResponse(
      id = user._id.getOrElse(throw new IllegalStateException("User must have an id")),
      email = user.primaryEmailAddress,
      displayName = user.publicFields.flatMap(_.displayName),
      vanityUrl = user.publicFields.flatMap(_.vanityUrl),
      personalDetails = PersonalDetails(
        firstName = user.privateFields.flatMap(_.firstName),
        lastName = user.privateFields.flatMap(_.secondName),
        gender = user.privateFields.flatMap(_.gender),
        dateOfBirth = user.dates.flatMap(_.birthDate.map(_.toLocalDate)),
        location = user.publicFields.flatMap(_.location)
      ),
      deliveryAddress = Address(
        addressLine1 = user.privateFields.flatMap(_.address1),
        addressLine2 = user.privateFields.flatMap(_.address2),
        addressLine3 = user.privateFields.flatMap(_.address3),
        addressLine4 = user.privateFields.flatMap(_.address4),
        country = user.privateFields.flatMap(_.country),
        postcode = user.privateFields.flatMap(_.postcode)
      ),
      billingAddress = Address(
        addressLine1 = user.privateFields.flatMap(_.billingAddress1),
        addressLine2 = user.privateFields.flatMap(_.billingAddress2),
        addressLine3 = user.privateFields.flatMap(_.billingAddress3),
        addressLine4 = user.privateFields.flatMap(_.billingAddress4),
        country = user.privateFields.flatMap(_.billingCountry),
        postcode = user.privateFields.flatMap(_.billingPostcode)
      ),
      lastActivityDate = user.dates.flatMap(_.lastActivityDate),
      lastActivityIp = user.privateFields.flatMap(_.lastActiveIpAddress),
      registrationDate = user.dates.flatMap(_.accountCreatedDate),
      registrationIp = user.privateFields.flatMap(_.registrationIp),
      status = UserStatus(
        receive3rdPartyMarketing = user.statusFields.flatMap(_.receive3rdPartyMarketing),
        receiveGnmMarketing = user.statusFields.flatMap(_.receiveGnmMarketing),
        userEmailValidated = user.statusFields.flatMap(_.userEmailValidated)
      ),
      groups = user.userGroups.map(_.map(g =>
        UserGroup(g.packageCode, g.joinedDate)
      ).toList).getOrElse(Nil)
    )
}