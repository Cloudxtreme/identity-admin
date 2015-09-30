package googlegroups



object DirectoryApiResponses {
  trait AccessOk
  case object AccessOk extends AccessOk

  case class AccessError(msg: String) {
    def ++(err: AccessError): AccessError = {
      if (err.msg.isEmpty) this
      else AccessError(this.msg ++ " & " ++ err.msg)
    }
  }

  val no2FAMembership = AccessError("You are not in the 2FA group")
  val noAdminMembership = AccessError("You are not in the Identity Admin group")
}
