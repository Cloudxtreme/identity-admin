package models

import play.api.libs.json._
import play.api.mvc.{Results, Result}
import scala.language.implicitConversions

case class SearchResponse(total: Int,
                          hasMore: Boolean,
                          results: Seq[UserSummary] = Nil)

object SearchResponse {
  implicit val format = Json.format[SearchResponse]

  implicit def searchResponseToResult(searchResponse: SearchResponse): Result =
    Results.Ok(Json.toJson(searchResponse))

}
