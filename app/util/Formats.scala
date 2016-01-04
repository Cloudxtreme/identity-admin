package util

import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat

object Formats {

  // http://tools.ietf.org/html/rfc7231#section-7.1.1.2
  private val HTTPDateFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZone(DateTimeZone.forID("GMT"))

  def toHttpDateTimeString(dateTime: DateTime): String = dateTime.withZone(DateTimeZone.forID("GMT")).toString(Formats.HTTPDateFormat)
  def toDateTime(date: String): DateTime = HTTPDateFormat.parseDateTime(date)
}
