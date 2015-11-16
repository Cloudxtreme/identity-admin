package services

import config.Config
import models.SearchResponse
import org.scalatest.{WordSpec, Matchers}
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest

class AdminApiTest extends WordSpec with Matchers{

  val requestSigner = new RequestSigner {
    override def secret: String = "secret"
    override def sign(request: WSRequest): WSRequest = request
  }

  val conf = new Config {
    val baseUrl = "baseUrl"
    val baseRootUrl = "baseRootUrl"
    val errorEmail = "errorEmail"
  }

  val adminApi = new AdminApi(conf, requestSigner)

  "checkResponse" should {
    "determine invalid json from the API is handled" in {
      val test = "{}"
      val response = adminApi.checkResponse[SearchResponse](200, test, 200, x => Json.parse(x).as[SearchResponse])
      response should be (Left(CustomError("Fatal Error", adminApi.contact)))
    }
  }
}
