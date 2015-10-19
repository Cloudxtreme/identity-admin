package models

import play.api.libs.json._

object CustomFormats {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val dateReads = Reads.jodaDateReads(dateFormat)

} 