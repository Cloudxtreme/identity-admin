package services

import org.scalatest.{Matchers, FlatSpec}

class AdminApiTest extends FlatSpec with Matchers{

  it should "determine invalid json from the API is handled" in {
    val test = "{}"
    val response = AdminApi.checkResponse(200, test)
    response should be (Left(CustomError("Fatal Error", "Contact identity team.")))
  }
}

