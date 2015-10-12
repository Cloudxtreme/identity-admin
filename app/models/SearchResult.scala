package models

import org.joda.time.DateTime

case class SearchResult(email: String, username: String, name: String,
                        registeredDate: DateTime, lastActivityDate: DateTime)

object SearchResult {

  def all(): List[SearchResult] =
    Nil

  def mockData(): SearchResult = new SearchResult("email@email.com", "myUsername", "FirstName Surname", new DateTime(2015, 4, 15, 16, 12, 0),
    new DateTime(2015, 4, 15, 16, 12, 0))
}
