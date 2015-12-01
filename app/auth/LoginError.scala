package auth

import play.api.mvc.QueryStringBindable

sealed trait LoginError

case class GroupsValidationFailed() extends LoginError
case class IdentityValidationFailed() extends LoginError
case class CSRFValidationFailed() extends LoginError
case class AccessForbidden() extends LoginError

object LoginError {
  implicit def queryStringBinder = new QueryStringBindable[LoginError] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LoginError]] = {
      params.get(key).map {
        case seq if seq.length > 1 => {
          Left("Only one login error is allowed")
        }
        case Seq(uniqueValue) => uniqueValue match {
          case "GroupsValidationFailed" => Right(GroupsValidationFailed())
          case "IdentityValidationFailed" => Right(IdentityValidationFailed())
          case "CSRFValidationFailed" => Right(CSRFValidationFailed())
          case "AccessForbidden" => Right(AccessForbidden())
          case _ => Left("Login error did not match any known errors")
        }
      }
    }

    override def unbind(key: String, loginError: LoginError): String = {
      loginError match {
        case GroupsValidationFailed() => s"$key=GroupsValidationFailed"
        case IdentityValidationFailed() => s"$key=IdentityValidationFailed"
        case CSRFValidationFailed() => s"$key=CSRFValidationFailed"
        case AccessForbidden() => s"$key=AccessForbidden"
      }
    }
  }
}

