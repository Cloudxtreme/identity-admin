package models

import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper

object CustomFormats {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val dateReads = Reads.jodaDateReads(dateFormat)

  implicit val objectMapFormat = new Format[Map[String, Any]] {

    def writes(map: Map[String, Any]): JsValue =
      Json.obj(map.map{case (s, o) =>
        val ret:(String, JsValueWrapper) = o match {
          case _:String => s -> JsString(o.asInstanceOf[String])
          case _:Int => s -> JsNumber(s.asInstanceOf[Int])
          case _:Long => s -> JsNumber(s.asInstanceOf[Long])
          case _:Double => s -> JsNumber(s.asInstanceOf[Double])
          case _:Boolean => s -> JsBoolean(s.asInstanceOf[Boolean])
          case other => s -> JsString(other.toString)
        }
        ret
      }.toSeq:_*)

    def reads(jv: JsValue): JsResult[Map[String, Any]] =
      JsSuccess(jv.as[Map[String, JsValue]].map{case (k, v) =>
        k -> (v match {
          case s:JsString => s.as[String]
          case i:JsNumber => i.as[Long]
          case b:JsBoolean => b.as[Boolean]
          case other => other.toString()
        })
      })
  }

} 