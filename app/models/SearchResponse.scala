package models

import play.api.libs.json._
import scala.language.implicitConversions

case class SearchResponse(total: Int,
                          hasMore: Boolean,
                          results: Seq[UserSummary] = Nil)

object SearchResponse {
  implicit val format = Json.format[SearchResponse]
}
