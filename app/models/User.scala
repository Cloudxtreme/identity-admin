package models

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json.Json
import scala.language.implicitConversions
import CustomFormats._


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

case class User(id: String,
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

object User {
  implicit val format = Json.format[User]
}