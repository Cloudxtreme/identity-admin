package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.implicitConversions
import CustomFormats._


case class UserSummary(id: String,
                       email: String,
                       username: Option[String],
                       firstName: Option[String],
                       lastName: Option[String],
                       creationDate: Option[DateTime],
                       lastActivityDate: Option[DateTime],
                       registrationIp: Option[String],
                       lastActiveIpAddress: Option[String])



object UserSummary {

  implicit val format = Json.format[UserSummary]

}
