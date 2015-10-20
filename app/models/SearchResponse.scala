package models

import play.api.libs.json._
import play.api.mvc.{Results, Result}
import repositories.User
import scala.language.implicitConversions

case class SearchResponse(total: Int,
                          hasMore: Boolean,
                          results: Seq[UserSummary] = Nil)

object SearchResponse {
  implicit val format = Json.format[SearchResponse]

  def create(json: JsValue): SearchResponse = {
    val total = (json\"total").as[Int]
    val hasMore = (json\"hasMore").as[Boolean]
    val results = (json\"results").as[Seq[UserSummary]]
    SearchResponse(total, hasMore, results)
  }

  implicit def searchResponseToResult(searchResponse: SearchResponse): Result =
    Results.Ok(Json.toJson(searchResponse))

  def create(total: Int, offset: Int, results: Seq[User]): SearchResponse = {
    val hasMore = (offset + results.size) < total
    SearchResponse(total, hasMore, results.map(UserSummary.fromUser))
  }
}
