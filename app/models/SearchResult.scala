package models

case class SearchResult(email: String, username: String, name: String,
                        registeredDate: String, lastActivityDate: String)

object SearchResult {

  def all(): List[SearchResult] =
    Nil

  def mockData(): SearchResult = new SearchResult("email@email.com", "myUsername", "FirstName Surname", "05 Apr 2015",
    "06 Apr 2015 12:30:31")
}
