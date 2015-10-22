package services

import models.SearchResponse
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json
import scala.language.implicitConversions

class AdminApiTest extends FlatSpec with Matchers{

  it should "Return SearchResponse object" in {
    val test = "{}"
    Json.parse(test).as[CustomError]
  }
//
////  val call = mock[Call]
////  val adminApi = new AdminApi(call)
//
////  it should "Return populated search response on successful query" in {
////    when(call.get("123")).thenReturn(Future(new FakeResponse(400, "Bad request")))
////    adminApi.getUsers("123") should be (SearchResponse(0, false) )
////  }
//
//  val port = 8080
//  val host = "localhost"
//  val wireMockServer = new WireMockServer(wireMockConfig().port(port))
//
//  def beforeEach = {
//    wireMockServer.start()
//    WireMock.configureFor(host, port)
//  }
//
//  def afterEach = {
//    wireMockServer.stop
//  }
//
//
//
}

