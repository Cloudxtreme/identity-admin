package models

import org.joda.time.DateTime

case class SearchResult(email: String, username: String, name: String,
                        registeredDate: DateTime, lastActivityDate: DateTime)

object SearchResult {

  def mockData(): SearchResult = {

    val mockDate = new DateTime(2015, 4, 15, 16, 12, 0)

    SearchResult("email@email.com",
                 "myUsername",
                 "FirstName Surname",
                 mockDate,
                 mockDate)
  }
}
