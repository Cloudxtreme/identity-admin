package auth

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class QueryStringBinderSpec extends FlatSpec with Matchers {

  "bind" should "bind a valid query string value into a LoginError" in {
    // Attempt at mimicking the behavior of the app when it is running,
    // as the type of the params value is actually scala.collection.convert.Wrappers$JListWrapper, which extends mutable.Buffer
    val params = Map("loginError" -> mutable.Buffer("GroupsValidationFailed"))
      LoginError.queryStringBinder.bind("loginError", params) should be(Some(Right(GroupsValidationFailed())))
  }

   it should "fail if there are more than one key present" in {
     val params = Map("loginError" -> mutable.Buffer("GroupsValidationFailed", "IdentityValidationFailed"))
     LoginError.queryStringBinder.bind("loginError", params) should be(Some(Left("Only one login error is allowed")))
   }

   it should "fail if the value does not map to any known loginError" in {
     val params = Map("loginError" -> mutable.Buffer("UnknownError"))
      LoginError.queryStringBinder.bind("loginError", params) should be(Some(Left("Login error did not match any known errors")))
   }

  "unbind" should "transform a valid LoginError into a query parameter" in {
    LoginError.queryStringBinder.unbind("loginError", GroupsValidationFailed()) should be("loginError=GroupsValidationFailed")
  }
}
