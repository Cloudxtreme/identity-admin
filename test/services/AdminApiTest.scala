package services

import models.SearchResponse
import org.scalatest.{WordSpec, Matchers}
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest

class AdminApiTest extends WordSpec with Matchers{

  val requestSigner = new RequestSigner {
    override def secret: String = "secret"
    override def sign(request: WSRequest): WSRequest = request
  }


  class TestAdminApi(requestSigner: RequestSigner) extends AdminApi(requestSigner: RequestSigner) {
    override lazy val errorEmail = "test@theguardian.com"
  }

  val adminApi = new TestAdminApi(requestSigner)

  "checkResponse" should {
    "determine invalid json from the API is handled" in {
      val test = "{}"
      val response = adminApi.checkResponse[SearchResponse](200, test, 200, x => Json.parse(x).as[SearchResponse])
      response should be (Left(CustomError("Fatal Error", adminApi.contact)))
    }
  }
}
