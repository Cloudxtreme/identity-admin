package services

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.ws.WSRequest

class AdminApiTest extends FlatSpec with Matchers{

  val requestSigner = new RequestSigner {
    override def secret: String = "secret"
    override def sign(request: WSRequest): WSRequest = request
  }

  val adminApi = new AdminApi(requestSigner)

  it should "determine invalid json from the API is handled" in {
    val test = "{}"
    val response = adminApi.checkResponse(200, test)
    response should be (Left(CustomError("Fatal Error", "Contact identity team.")))
  }
}
